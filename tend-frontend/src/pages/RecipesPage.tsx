
import { useEffect, useState } from 'react'
import { Plus, Search } from 'lucide-react'
import { useNavigate } from 'react-router-dom'

import RecipeHeader from '../features/recipes/components/RecipeHeader'
import SavedRecipeCard from '../features/recipes/components/SavedRecipeCard'
import { getSavedRecipes } from '../features/recipes/api/getSavedRecipes'
import { useApi } from '../hooks/useApi'
import type { SavedRecipe } from '../types/SavedRecipe'

export default function RecipesPage() {
    const { apiFetch } = useApi()
    const navigate = useNavigate()

    const [recipes, setRecipes] = useState<SavedRecipe[]>([])
    const [isLoading, setIsLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [searchQuery, setSearchQuery] = useState('')
    const [isSearching, setIsSearching] = useState(false)

    useEffect(() => {
        const controller = new AbortController()

        getSavedRecipes(apiFetch, controller.signal)
            .then(setRecipes)
            .catch(err => {
                if (!controller.signal.aborted) {
                    setError(err instanceof Error ? err.message : 'Failed to load recipes')
                }
            })
            .finally(() => {
                if (!controller.signal.aborted) {
                    setIsLoading(false)
                }
            })

        return () => controller.abort()
    }, [apiFetch])

    const filteredRecipes = recipes.filter(recipe =>
        recipe.name.toLowerCase().includes(searchQuery.toLowerCase())
    )

    return (
        <div className="min-h-dvh bg-canvas px-6 pb-12 pt-24">
            <div className="mx-auto w-full max-w-4xl">

                <RecipeHeader />

                <section>
                    <div className="mb-6 flex items-center justify-between">
                        <h2 className="font-serif text-2xl text-ink">
                            Your collection
                        </h2>

                        <div className="flex items-center gap-4">
                            <button
                                type="button"
                                aria-label="Search recipes"
                                onClick={() => {
                                    setIsSearching(!isSearching)
                                    setSearchQuery('')
                                }}
                                className="text-primary transition-colors hover:text-ink"
                            >
                                <Search size={21} strokeWidth={1.5} />
                            </button>

                            <button
                                type="button"
                                aria-label="Create recipe"
                                onClick={() => navigate('/recipeEditor')}
                                className="text-primary transition-colors hover:text-ink"
                            >
                                <Plus size={23} strokeWidth={1.5} />
                            </button>
                        </div>
                    </div>

                    {isSearching && (
                        <input
                            type="search"
                            autoFocus
                            value={searchQuery}
                            onChange={e => setSearchQuery(e.target.value)}
                            placeholder="Search your recipes..."
                            className="mb-6 w-full rounded-xl border border-primary/15 bg-white/50 px-4 py-3 text-sm text-ink outline-none placeholder:text-primary/60 focus:border-primary/40"
                        />
                    )}

                    {isLoading ? (
                        <p className="text-sm text-primary">Loading recipes...</p>
                    ) : error ? (
                        <p className="text-sm text-red-600">{error}</p>
                    ) : recipes.length === 0 ? (
                        <div className="rounded-2xl border border-primary/10 bg-white/40 px-6 py-16 text-center">
                            <p className="font-serif text-xl text-ink">
                                Your recipe collection is waiting.
                            </p>
                            <p className="mt-3 text-sm text-primary">
                                Save recipes you love or create something entirely your own.
                            </p>
                        </div>
                    ) : filteredRecipes.length === 0 ? (
                        <p className="text-sm text-primary">
                            No recipes match your search.
                        </p>
                    ) : (
                        <div className="grid min-w-0 grid-cols-1 gap-4 sm:grid-cols-2">
                            {filteredRecipes.map(recipe => (
                                <SavedRecipeCard
                                    key={recipe.id}
                                    recipe={recipe}
                                />
                            ))}
                        </div>
                    )}
                </section>
            </div>
        </div>
    )
}
