
import { ChevronRight, Utensils } from 'lucide-react'
import type { SavedRecipe } from '../../../types/SavedRecipe'

type SavedRecipeCardProps = {
    recipe: SavedRecipe
    onClick?: (recipe: SavedRecipe) => void
}

export default function SavedRecipeCard({
                                            recipe,
                                            onClick
                                        }: SavedRecipeCardProps) {
    return (
        <button
            type="button"
            onClick={() => onClick?.(recipe)}
            className="
                group flex w-full min-w-0 max-w-full
                items-center gap-3 overflow-hidden
                rounded-2xl border border-primary/10
                bg-white/40 p-3 text-left
                transition-all duration-200
                hover:border-primary/25 hover:bg-white/60
                focus-visible:outline-2 focus-visible:outline-offset-2
                focus-visible:outline-primary
                sm:gap-4 sm:p-4
            "
        >
            <div
                className="
                    flex h-14 w-14 shrink-0
                    items-center justify-center
                    rounded-xl bg-surface text-primary
                    sm:h-16 sm:w-16
                "
            >
                <Utensils size={24} strokeWidth={1.4} />
            </div>

            <div className="min-w-0 flex-1 overflow-hidden">
                <h3 className="truncate font-serif text-base text-ink sm:text-lg">
                    {recipe.name}
                </h3>

                <p className="mt-1 text-xs text-primary/70">
                    Saved recipe
                </p>
            </div>

            <ChevronRight
                size={19}
                strokeWidth={1.5}
                className="
                    shrink-0 text-primary/50
                    transition-transform duration-200
                    group-hover:translate-x-1
                    group-hover:text-primary
                "
            />
        </button>
    )
}
