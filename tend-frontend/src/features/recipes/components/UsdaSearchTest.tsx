
import { useEffect, useState } from 'react'
import { useApi } from '../../../hooks/useApi'

type UsdaFoodSearchResult = {
    fdcId: string
    description: string
    dataType: string
}

export default function UsdaSearchTest() {
    const { apiFetch } = useApi()

    const [query, setQuery] = useState('')
    const [foods, setFoods] = useState<UsdaFoodSearchResult[]>([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        const searchTerm = query.trim()

        if (searchTerm.length < 2) {
            setFoods([])
            setLoading(false)
            setError(null)
            return
        }

        const controller = new AbortController()

        // Clear previous results while waiting for the new search.
        setFoods([])
        setLoading(true)
        setError(null)

        // Wait briefly so we don't query MySQL on every keystroke.
        const timeout = setTimeout(async () => {
            try {
                const response = await apiFetch(
                    `/api/foods/search?q=${encodeURIComponent(searchTerm)}`,
                    { signal: controller.signal }
                )

                if (!response.ok) {
                    throw new Error(`Search failed: ${response.status}`)
                }

                const data: UsdaFoodSearchResult[] =
                    await response.json()

                if (!controller.signal.aborted) {
                    setFoods(data)
                }
            } catch (err) {
                if (controller.signal.aborted) return

                setError(
                    err instanceof Error
                        ? err.message
                        : 'Search failed'
                )
            } finally {
                if (!controller.signal.aborted) {
                    setLoading(false)
                }
            }
        }, 100)

        // Cancel outdated searches when the user types again.
        return () => {
            clearTimeout(timeout)
            controller.abort()
        }
    }, [query, apiFetch])

    return (
        <div className="my-8 rounded-2xl border border-primary/20 p-6">
            <input
                type="search"
                value={query}
                onChange={event => setQuery(event.target.value)}
                placeholder="Search foods..."
                className="w-full rounded-lg border border-primary/20 bg-white/50 px-4 py-3 text-ink outline-none focus:border-primary"
            />

            {loading && (
                <p className="mt-4 text-sm text-primary">
                    Searching...
                </p>
            )}

            {error && (
                <p className="mt-4 text-sm text-red-600">
                    {error}
                </p>
            )}

            {!loading && !error && query.trim().length >= 2
                && foods.length === 0 && (
                    <p className="mt-4 text-sm text-primary">
                        No foods found.
                    </p>
                )}

            <ul className="mt-4 space-y-3">
                {foods.map(food => (
                    <li
                        key={food.fdcId}
                        className="border-b border-primary/10 pb-3 last:border-0"
                    >
                        <p className="text-sm text-ink">
                            {food.description}
                        </p>
                        <p className="mt-1 text-xs text-primary">
                            {food.dataType}
                        </p>
                    </li>
                ))}
            </ul>
        </div>
    )
}
