
export interface CreateRecipeRequest {
    name: string
    description: string
    servings: number
    ingredients: {
        usdaFdcId: string | null
        quantity: number
        unit: string
    }[]
    instructions: string[]
}

export interface CreatedRecipeResponse {
    id: number
    foodId: number
    name: string
}

type ApiFetch = (
    input: string,
    init?: RequestInit
) => Promise<Response>

export async function createRecipe(
    apiFetch: ApiFetch,
    recipe: CreateRecipeRequest
): Promise<CreatedRecipeResponse> {
    const response = await apiFetch('/api/recipes', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(recipe),
    })

    if (!response.ok) {
        throw new Error(
            `Failed to create recipe (${response.status})`
        )
    }

    return response.json()
}
