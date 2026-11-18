
import { useEffect, useState } from 'react'
import { Plus, Search } from 'lucide-react'

import KitchenHeader from '../features/kitchen/components/KitchenHeader.tsx'
import UsdaSearchTest from '../features/kitchen/components/UsdaSearchTest'
import { useApi } from '../hooks/useApi'


/**
 * Matches KitchenItemResponse.java from Spring Boot.
 */
type KitchenItem = {
    id: number
    foodId: number
    name: string
}

export default function RecipesPage() {

    // Gives us the authenticated API request function.
    const { apiFetch } = useApi()

    // Stores the Foods returned by Spring Boot.
    const [items, setItems] = useState<KitchenItem[]>([])

    // Tracks whether we're waiting for a response.
    const [isLoading, setIsLoading] = useState(true)

    // Stores any error that occurs while loading.
    const [error, setError] = useState<string | null>(null)

    /**
     * Runs when the component mounts or apiFetch changes.
     */
    useEffect(() => {
        const controller = new AbortController()

        async function loadKitchen() {
            setIsLoading(true)
            setError(null)

            try {
                // Request the authenticated user's Kitchen.
                const response = await apiFetch('/api/kitchen', {
                    signal: controller.signal
                })

                // Convert the JSON response into JavaScript objects.
                const data: KitchenItem[] = await response.json()

                // Store the Foods in React state.
                if (!controller.signal.aborted) {
                    setItems(data)
                }

            } catch (err) {
                // Ignore requests canceled during cleanup.
                if (controller.signal.aborted) return

                setError(
                    err instanceof Error
                        ? err.message
                        : 'Could not load Kitchen'
                )

            } finally {
                if (!controller.signal.aborted) {
                    setIsLoading(false)
                }
            }
        }

        loadKitchen()

        // Cancel the request if the component unmounts.
        return () => controller.abort()

    }, [apiFetch])

    return (
        <div className="min-h-dvh bg-canvas px-6 pb-12 pt-24">
            <div className="mx-auto w-full max-w-4xl">

                <KitchenHeader />

                <UsdaSearchTest />

                <section>
                    <div className="mb-6 flex items-center justify-between">

                        <h2 className="font-serif text-2xl text-ink">
                            Your collection
                        </h2>

                        <div className="flex items-center gap-4">
                            <button
                                type="button"
                                aria-label="Search collection"
                                className="text-primary transition-colors hover:text-ink"
                            >
                                <Search size={21} strokeWidth={1.5} />
                            </button>

                            <button
                                type="button"
                                aria-label="Add to kitchen"
                                className="text-primary transition-colors hover:text-ink"
                            >
                                <Plus size={23} strokeWidth={1.5} />
                            </button>
                        </div>
                    </div>

                    {isLoading ? (
                        <p className="text-sm text-primary">
                            Loading your kitchen...
                        </p>
                    ) : error ? (
                        <p className="text-sm text-red-600">
                            {error}
                        </p>
                    ) : items.length === 0 ? (
                        <div className="rounded-2xl border border-primary/10 bg-white/40 px-6 py-16 text-center">
                            <p className="font-serif text-xl text-ink">
                                Your kitchen is waiting.
                            </p>

                            <p className="mt-3 text-sm text-primary">
                                Your favorite foods and meals will find a home here.
                            </p>
                        </div>
                    ) : (
                        <div className="grid gap-4 sm:grid-cols-2">
                            {items.map(item => (
                                <div
                                    key={item.id}
                                    className="rounded-2xl border border-primary/10 bg-white/40 p-6"
                                >
                                    <h3 className="font-serif text-xl text-ink">
                                        {item.name}
                                    </h3>
                                </div>
                            ))}
                        </div>
                    )}

                </section>
            </div>
        </div>
    )
}
