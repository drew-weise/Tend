
import type { SavedRecipe } from '../../../types/SavedRecipe'

export async function getSavedRecipes(
    apiFetch: (
        endpoint: string,
        options?: RequestInit
    ) => Promise<Response>,
    signal?: AbortSignal
): Promise<SavedRecipe[]> {

    const response = await apiFetch('/api/recipes', {
        signal
    })

    if (!response.ok) {
        throw new Error('Failed to load saved recipes')
    }

    return response.json()
}
