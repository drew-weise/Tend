
import { useState } from 'react'
import { Plus, Trash2, Check } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { useApi } from '../hooks/useApi'
import { createRecipe } from '../features/recipes/api/createRecipe'
import IngredientFoodSearch from '../features/recipes/components/IngredientFoodSearch'

export interface RecipeFood {
    id: string
    usdaFdcId: string | null
    name: string
    quantity: string
    unit: string
}

export interface RecipeDraft {
    name: string
    description: string
    servings: number
    ingredients: RecipeFood[]
    instructions: string[]
}

const createIngredient = (): RecipeFood => ({
    id: crypto.randomUUID(),
    usdaFdcId: null,
    name: '',
    quantity: '',
    unit: 'g',
})

const createEmptyRecipe = (): RecipeDraft => ({
    name: '',
    description: '',
    servings: 1,
    ingredients: [createIngredient()],
    instructions: [''],
})

const inputClass =
    'w-full min-w-0 rounded-xl border border-primary/15 bg-white/70 px-4 py-3 text-sm text-ink outline-none transition-colors placeholder:text-primary/50 focus:border-primary/60'

export default function RecipeEditorPage() {
    const { apiFetch } = useApi()
    const navigate = useNavigate()
    const [recipe, setRecipe] = useState<RecipeDraft>(
        createEmptyRecipe
    )
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState('')
    const [attemptedSave, setAttemptedSave] = useState(false)

    const nameInvalid =
        attemptedSave && !recipe.name.trim()

    const servingsInvalid =
        attemptedSave &&
        (!Number.isInteger(recipe.servings) || recipe.servings < 1)

    const ingredientsInvalid =
        attemptedSave &&
        (
            recipe.ingredients.length === 0 ||
            recipe.ingredients.some(ingredient =>
                !ingredient.usdaFdcId ||
                !ingredient.quantity.trim() ||
                !Number.isFinite(Number(ingredient.quantity)) ||
                Number(ingredient.quantity) <= 0 ||
                !ingredient.unit
            )
        )

    function updateRecipe<K extends keyof RecipeDraft>(
        field: K,
        value: RecipeDraft[K]
    ) {
        setRecipe(previous => ({
            ...previous,
            [field]: value,
        }))
    }

    function addIngredient() {
        updateRecipe('ingredients', [
            ...recipe.ingredients,
            createIngredient(),
        ])
    }

    function updateIngredient(
        id: string,
        field: 'name' | 'quantity' | 'unit',
        value: string
    ) {
        updateRecipe(
            'ingredients',
            recipe.ingredients.map(ingredient =>
                ingredient.id === id
                    ? {
                        ...ingredient,
                        [field]: value,
                        ...(field === 'name'
                            ? { usdaFdcId: null }
                            : {}),
                    }
                    : ingredient
            )
        )
    }

    function removeIngredient(id: string) {
        updateRecipe(
            'ingredients',
            recipe.ingredients.filter(
                ingredient => ingredient.id !== id
            )
        )
    }

    function updateInstruction(index: number, value: string) {
        updateRecipe(
            'instructions',
            recipe.instructions.map((step, i) =>
                i === index ? value : step
            )
        )
    }

    function removeInstruction(index: number) {
        updateRecipe(
            'instructions',
            recipe.instructions.filter((_, i) => i !== index)
        )
    }

    async function handleSave() {
        if (saving) return
        setAttemptedSave(true)
        setError('')

        if (!recipe.name.trim()) {
            setError('Please enter a recipe name.')
            return
        }

        if (!Number.isInteger(recipe.servings) || recipe.servings < 1) {
            setError('Servings must be a whole number of at least 1.')
            return
        }

        if (
            recipe.ingredients.length === 0 ||
            recipe.ingredients.some(ingredient =>
                !ingredient.usdaFdcId ||
                !ingredient.quantity.trim() ||
                !Number.isFinite(Number(ingredient.quantity)) ||
                Number(ingredient.quantity) <= 0 ||
                !ingredient.unit
            )
        ) {
            setError(
                'Select a food for every ingredient and enter a positive quantity and unit.'
            )
            return
        }

        setSaving(true)

        try {
            await createRecipe(apiFetch, {
                name: recipe.name.trim(),
                description: recipe.description.trim(),
                servings: recipe.servings,
                ingredients: recipe.ingredients.map(ingredient => ({
                    usdaFdcId: ingredient.usdaFdcId,
                    quantity: Number(ingredient.quantity),
                    unit: ingredient.unit,
                })),
                instructions: recipe.instructions.map(step => step.trim()).filter(Boolean),
            })
            navigate('/recipes')
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Could not save the recipe.')
        } finally {
            setSaving(false)
        }
    }

    return (
        <main className="min-h-dvh min-w-0 bg-canvas text-ink">
            <div className="mx-auto w-full max-w-3xl px-5 pb-20 pt-24 sm:px-8">
                <header className="mb-10 text-center">
                    <h1 className="font-serif text-4xl tracking-tight text-ink">
                        New Recipe
                    </h1>
                    <div className="mx-auto mt-4 h-px w-9 bg-accent" />
                </header>

                <div className="space-y-10">
                    <section className="space-y-5">
                        <div>
                            <label
                                htmlFor="recipe-name"
                                className="mb-2 flex items-center gap-2 text-sm font-medium"
                            >
                                Recipe name
                                <span className="text-red-600">*</span>
                            </label>
                            <input
                                id="recipe-name"
                                value={recipe.name}
                                onChange={event =>
                                    updateRecipe('name', event.target.value)
                                }
                                placeholder="e.g. Creamy Chicken Pasta"
                                aria-required="true"
                                aria-invalid={nameInvalid}
                                className={`${inputClass} ${
                                    nameInvalid ? 'border-red-500' : ''
                                }`}
                            />
                            {nameInvalid && (
                                <p className="mt-2 text-xs text-red-600">
                                    Recipe name is required.
                                </p>
                            )}
                        </div>

                        <div>
                            <label
                                htmlFor="recipe-description"
                                className="mb-2 block text-sm font-medium"
                            >
                                Description
                                <span className="ml-2 font-normal text-primary/60">
                                    Optional
                                </span>
                            </label>
                            <textarea
                                id="recipe-description"
                                value={recipe.description}
                                onChange={event =>
                                    updateRecipe('description', event.target.value)
                                }
                                placeholder="Tell us about your recipe..."
                                rows={3}
                                className={`${inputClass} resize-y`}
                            />
                        </div>

                        <div className="flex items-center justify-between gap-4 rounded-xl bg-surface/50 px-4 py-4">
                            <div>
                                <label
                                    htmlFor="recipe-servings"
                                    className="text-sm font-medium"
                                >
                                    Servings <span className="text-red-600">*</span>
                                </label>
                                <p className="mt-1 text-xs text-primary/70">
                                    How many portions does this make?
                                </p>
                            </div>
                            <input
                                id="recipe-servings"
                                type="number"
                                min={1}
                                step={1}
                                value={recipe.servings}
                                onChange={event =>
                                    updateRecipe(
                                        'servings',
                                        Number(event.target.value)
                                    )
                                }
                                aria-invalid={servingsInvalid}
                                className={`w-20 shrink-0 rounded-lg border bg-white px-2 py-2 text-center text-sm outline-none focus:border-primary/60 ${
                                    servingsInvalid
                                        ? 'border-red-500'
                                        : 'border-primary/15'
                                }`}
                            />
                        </div>
                    </section>

                    <div className="h-px bg-primary/10" />

                    <section className="space-y-5">
                        <div className="flex items-center justify-between gap-3">
                            <div>
                                <h2 className="font-serif text-2xl">
                                    Ingredients
                                    <span className="ml-2 font-sans text-sm text-red-600">
                                        *
                                    </span>
                                </h2>
                                <p className="mt-1 text-xs text-primary/70">
                                    At least one ingredient is required.
                                </p>
                            </div>
                            <button
                                type="button"
                                onClick={addIngredient}
                                className="inline-flex shrink-0 items-center gap-1.5 rounded-lg px-3 py-2 text-sm font-medium text-primary hover:bg-surface"
                            >
                                <Plus size={17} />
                                Add
                            </button>
                        </div>

                        <div className="space-y-3">
                            {recipe.ingredients.map((ingredient, index) => {
                                const invalid =
                                    attemptedSave &&
                                    (
                                        !ingredient.usdaFdcId ||
                                        !ingredient.quantity.trim() ||
                                        !Number.isFinite(Number(ingredient.quantity)) ||
                                        Number(ingredient.quantity) <= 0 ||
                                        !ingredient.unit
                                    )

                                return (
                                    <div
                                        key={ingredient.id}
                                        className={`min-w-0 rounded-2xl border bg-white/40 p-4 ${
                                            invalid
                                                ? 'border-red-400'
                                                : 'border-primary/10'
                                        }`}
                                    >
                                        <div className="mb-3 flex items-center justify-between">
                                            <span className="text-xs font-medium text-primary/70">
                                                Ingredient {index + 1}
                                            </span>
                                            <button
                                                type="button"
                                                onClick={() =>
                                                    removeIngredient(ingredient.id)
                                                }
                                                aria-label={`Remove ingredient ${index + 1}`}
                                                className="rounded-lg p-1.5 text-primary/60 hover:bg-red-50 hover:text-red-600"
                                            >
                                                <Trash2 size={17} strokeWidth={1.5} />
                                            </button>
                                        </div>

                                        <div className="space-y-3">
                                            <IngredientFoodSearch
                                                name={ingredient.name}
                                                selected={ingredient.usdaFdcId !== null}
                                                label={`Ingredient ${index + 1} food search`}
                                                onQueryChange={query => updateIngredient(ingredient.id, 'name', query)}
                                                onSelect={food => setRecipe(previous => ({
                                                    ...previous,
                                                    ingredients: previous.ingredients.map(item => item.id === ingredient.id
                                                        ? { ...item, name: food.name, usdaFdcId: food.usdaFdcId }
                                                        : item),
                                                }))}
                                            />

                                            <div className="grid grid-cols-2 gap-3">
                                                <div className="min-w-0">
                                                    <label className="mb-1.5 block text-xs text-primary/70">
                                                        Quantity
                                                    </label>
                                                    <input
                                                        type="number"
                                                        min="0.001"
                                                        step="any"
                                                        value={ingredient.quantity}
                                                        onChange={event =>
                                                            updateIngredient(
                                                                ingredient.id,
                                                                'quantity',
                                                                event.target.value
                                                            )
                                                        }
                                                        placeholder="Amount"
                                                        aria-label={`Ingredient ${index + 1} quantity`}
                                                        className={inputClass}
                                                    />
                                                </div>

                                                <div className="min-w-0">
                                                    <label className="mb-1.5 block text-xs text-primary/70">
                                                        Unit
                                                    </label>
                                                    <select
                                                        value={ingredient.unit}
                                                        onChange={event =>
                                                            updateIngredient(
                                                                ingredient.id,
                                                                'unit',
                                                                event.target.value
                                                            )
                                                        }
                                                        aria-label={`Ingredient ${index + 1} unit`}
                                                        className={inputClass}
                                                    >
                                                        <option value="g">Grams</option>
                                                        <option value="oz">Ounces</option>
                                                        <option value="cup">Cups</option>
                                                        <option value="tbsp">Tablespoons</option>
                                                        <option value="tsp">Teaspoons</option>
                                                        <option value="each">Each</option>
                                                    </select>
                                                </div>
                                            </div>
                                        </div>

                                        {invalid && (
                                            <p className="mt-3 text-xs text-red-600">
                                                Select a food and complete the quantity and unit.
                                            </p>
                                        )}
                                    </div>
                                )
                            })}
                        </div>

                        {recipe.ingredients.length === 0 && (
                            <div className="rounded-2xl border border-dashed border-primary/20 px-5 py-8 text-center">
                                <p className="text-sm text-primary">
                                    No ingredients added yet.
                                </p>
                                <button
                                    type="button"
                                    onClick={addIngredient}
                                    className="mt-3 text-sm font-medium text-primary underline underline-offset-4"
                                >
                                    Add your first ingredient
                                </button>
                            </div>
                        )}

                        {ingredientsInvalid && recipe.ingredients.length === 0 && (
                            <p className="text-xs text-red-600">
                                Add at least one ingredient.
                            </p>
                        )}
                    </section>

                    <div className="h-px bg-primary/10" />

                    <section className="space-y-5">
                        <div className="flex items-center justify-between gap-3">
                            <div>
                                <h2 className="font-serif text-2xl">
                                    Instructions
                                </h2>
                                <p className="mt-1 text-xs text-primary/70">
                                    Optional
                                </p>
                            </div>
                            <button
                                type="button"
                                onClick={() =>
                                    updateRecipe('instructions', [
                                        ...recipe.instructions,
                                        '',
                                    ])
                                }
                                className="inline-flex shrink-0 items-center gap-1.5 rounded-lg px-2 py-2 text-sm font-medium text-primary hover:bg-surface"
                            >
                                <Plus size={17} />
                                Step
                            </button>
                        </div>

                        <div className="space-y-4">
                            {recipe.instructions.map((step, index) => (
                                <div
                                    key={index}
                                    className="flex min-w-0 items-start gap-3"
                                >
                                    <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-surface font-serif text-base text-primary">
                                        {index + 1}
                                    </div>
                                    <textarea
                                        value={step}
                                        onChange={event =>
                                            updateInstruction(index, event.target.value)
                                        }
                                        placeholder="Describe this step..."
                                        aria-label={`Instruction step ${index + 1}`}
                                        rows={3}
                                        className={`${inputClass} flex-1 resize-y`}
                                    />
                                    <button
                                        type="button"
                                        onClick={() => removeInstruction(index)}
                                        aria-label={`Remove step ${index + 1}`}
                                        className="shrink-0 rounded-lg p-2 text-primary/60 hover:text-red-600"
                                    >
                                        <Trash2 size={17} strokeWidth={1.5} />
                                    </button>
                                </div>
                            ))}
                        </div>
                    </section>

                    {error && (
                        <p role="alert" className="text-sm text-red-700">
                            {error}
                        </p>
                    )}

                    <div className="space-y-4 border-t border-primary/10 pt-6">
                        <button
                            type="button"
                            onClick={handleSave}
                            disabled={saving}
                            className="flex w-full items-center justify-center gap-2 rounded-xl bg-primary px-5 py-4 text-sm font-semibold text-white transition-colors hover:bg-primary/90 disabled:opacity-50"
                        >
                            <Check size={18} strokeWidth={2} />
                            {saving ? 'Saving...' : 'Save Recipe'}
                        </button>
                        <p className="text-center text-xs text-primary/60">
                            * Required fields
                        </p>
                    </div>
                </div>
            </div>
        </main>
    )
}
