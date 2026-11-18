import { useState } from 'react'
import { useApi } from '../../../hooks/useApi.ts'

type UsdaFoodSearchResult = {
    fdcId: string
    description: string
    dataType: string
}

export default function UsdaSearchTest() {
    const { apiFetch } = useApi()
    const [foods, setFoods] = useState<UsdaFoodSearchResult[]>([])
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState(false)

    async function searchFoods() {
        setLoading(true)
        setError(null)

        try {
            const response = await apiFetch(
                '/api/foods/search?q=chicken'
            )

            if (!response.ok) {
                throw new Error(`Search failed: ${response.status}`)
            }

            const data: UsdaFoodSearchResult[] = await response.json()
            setFoods(data)
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Search failed')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="my-8 rounded-2xl border border-primary/20 p-6">
            <button
                onClick={searchFoods}
                disabled={loading}
                className="rounded-lg bg-primary px-4 py-2 text-white"
            >
                {loading ? 'Searching...' : 'Test USDA Search'}
            </button>

            {error && (
                <p className="mt-4 text-red-600">{error}</p>
            )}

            <ul className="mt-4 space-y-2">
                {foods.map(food => (
                    <li key={food.fdcId} className="text-sm text-ink">
                        {food.description} ({food.dataType})
                    </li>
                ))}
            </ul>
        </div>
    )
}