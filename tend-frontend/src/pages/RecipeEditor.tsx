
import { useState } from "react";

export interface RecipeIngredient {
    id: string;
    foodId: number | null;
    name: string;
    quantity: string;
    unit: string;
}

export interface RecipeDraft {
    name: string;
    description: string;
    servings: number;
    ingredients: RecipeIngredient[];
    instructions: string[];
}

interface RecipeEditorProps {
    initialRecipe?: RecipeDraft;
    onSave: (recipe: RecipeDraft) => Promise<void> | void;
    onCancel: () => void;
}

const emptyRecipe: RecipeDraft = {
    name: "",
    description: "",
    servings: 1,
    ingredients: [],
    instructions: [""],
};

export default function RecipeEditor({
                                         initialRecipe,
                                         onSave,
                                         onCancel,
                                     }: RecipeEditorProps) {
    const [recipe, setRecipe] = useState<RecipeDraft>(
        () => initialRecipe ?? emptyRecipe
    );

    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    function updateRecipe<K extends keyof RecipeDraft>(
        field: K,
        value: RecipeDraft[K]
    ) {
        setRecipe((previous) => ({
            ...previous,
            [field]: value,
        }));
    }

    function addIngredient() {
        updateRecipe("ingredients", [
            ...recipe.ingredients,
            {
                id: crypto.randomUUID(),
                foodId: null,
                name: "",
                quantity: "",
                unit: "g",
            },
        ]);
    }

    function updateIngredient(
        id: string,
        field: keyof RecipeIngredient,
        value: string
    ) {
        updateRecipe(
            "ingredients",
            recipe.ingredients.map((ingredient) =>
                ingredient.id === id
                    ? {
                        ...ingredient,
                        [field]:
                            field === "name"
                                ? value
                                : field === "quantity" || field === "unit"
                                    ? value
                                    : ingredient[field],
                        ...(field === "name" ? { foodId: null } : {}),
                    }
                    : ingredient
            )
        );
    }

    function removeIngredient(id: string) {
        updateRecipe(
            "ingredients",
            recipe.ingredients.filter(
                (ingredient) => ingredient.id !== id
            )
        );
    }

    function updateInstruction(index: number, value: string) {
        updateRecipe(
            "instructions",
            recipe.instructions.map((step, i) =>
                i === index ? value : step
            )
        );
    }

    function removeInstruction(index: number) {
        updateRecipe(
            "instructions",
            recipe.instructions.filter((_, i) => i !== index)
        );
    }

    async function handleSave() {
        if (!recipe.name.trim()) {
            setError("Please enter a recipe name.");
            return;
        }

        if (recipe.ingredients.some(
            (ingredient) =>
                !ingredient.name.trim() ||
                !ingredient.quantity.trim() ||
                Number(ingredient.quantity) <= 0 ||
                !Number.isFinite(Number(ingredient.quantity))
        )) {
            setError("Please complete all ingredient names and quantities.");
            return;
        }

        setSaving(true);
        setError("");

        try {
            await onSave({
                ...recipe,
                name: recipe.name.trim(),
                ingredients: recipe.ingredients.map((ingredient) => ({
                    ...ingredient,
                    name: ingredient.name.trim(),
                })),
                instructions: recipe.instructions
                    .map((step) => step.trim())
                    .filter(Boolean),
            });
        } catch {
            setError("Could not save the recipe. Please try again.");
        } finally {
            setSaving(false);
        }
    }

    return (
        <main className="min-h-dvh bg-[#F5F0E8] text-[#292F29]">
            <header className="sticky top-0 z-10 border-b border-[#536A55]/15 bg-[#F5F0E8]">
                <div className="mx-auto flex max-w-2xl items-center justify-between px-4 py-4">
                    <button type="button" onClick={onCancel}>
                        ← Back
                    </button>

                    <h1 className="font-semibold">
                        {initialRecipe ? "Edit Recipe" : "New Recipe"}
                    </h1>

                    <button
                        type="button"
                        onClick={handleSave}
                        disabled={saving}
                        className="font-semibold text-[#536A55] disabled:opacity-50"
                    >
                        {saving ? "Saving..." : "Save"}
                    </button>
                </div>
            </header>

            <div className="mx-auto flex max-w-2xl flex-col gap-8 px-4 py-6 pb-24">
                <section className="space-y-4">
                    <div>
                        <label className="mb-2 block text-sm font-medium">
                            Recipe Name
                        </label>
                        <input
                            value={recipe.name}
                            onChange={(event) =>
                                updateRecipe("name", event.target.value)
                            }
                            placeholder="e.g. Creamy Chicken Pasta"
                            className="w-full rounded-xl border border-[#536A55]/20 bg-white p-3 outline-none focus:border-[#536A55]"
                        />
                    </div>

                    <div>
                        <label className="mb-2 block text-sm font-medium">
                            Description
                        </label>
                        <textarea
                            value={recipe.description}
                            onChange={(event) =>
                                updateRecipe("description", event.target.value)
                            }
                            placeholder="Tell us about your recipe..."
                            rows={3}
                            className="w-full rounded-xl border border-[#536A55]/20 bg-white p-3 outline-none focus:border-[#536A55]"
                        />
                    </div>

                    <div className="flex items-center justify-between">
                        <label className="font-medium">Servings</label>
                        <input
                            type="number"
                            min={1}
                            value={recipe.servings}
                            onChange={(event) =>
                                updateRecipe(
                                    "servings",
                                    Math.max(1, Number(event.target.value) || 1)
                                )
                            }
                            className="w-20 rounded-xl border border-[#536A55]/20 bg-white p-2 text-center"
                        />
                    </div>
                </section>

                <section className="space-y-4">
                    <div className="flex items-center justify-between">
                        <h2 className="text-xl font-semibold">Ingredients</h2>
                        <button
                            type="button"
                            onClick={addIngredient}
                            className="font-medium text-[#536A55]"
                        >
                            + Add
                        </button>
                    </div>

                    {recipe.ingredients.map((ingredient) => (
                        <div
                            key={ingredient.id}
                            className="space-y-3 rounded-xl bg-[#E6E9DD] p-4"
                        >
                            <input
                                value={ingredient.name}
                                onChange={(event) =>
                                    updateIngredient(
                                        ingredient.id,
                                        "name",
                                        event.target.value
                                    )
                                }
                                placeholder="Search for an ingredient..."
                                className="w-full rounded-lg bg-white p-3 outline-none"
                            />

                            <div className="flex items-center gap-2">
                                <input
                                    type="number"
                                    min="0.001"
                                    step="any"
                                    value={ingredient.quantity}
                                    onChange={(event) =>
                                        updateIngredient(
                                            ingredient.id,
                                            "quantity",
                                            event.target.value
                                        )
                                    }
                                    placeholder="Amount"
                                    className="min-w-0 flex-1 rounded-lg bg-white p-3"
                                />

                                <select
                                    value={ingredient.unit}
                                    onChange={(event) =>
                                        updateIngredient(
                                            ingredient.id,
                                            "unit",
                                            event.target.value
                                        )
                                    }
                                    className="rounded-lg bg-white p-3"
                                >
                                    <option value="g">g</option>
                                    <option value="oz">oz</option>
                                    <option value="cup">cup</option>
                                    <option value="tbsp">tbsp</option>
                                    <option value="tsp">tsp</option>
                                    <option value="each">each</option>
                                </select>

                                <button
                                    type="button"
                                    onClick={() => removeIngredient(ingredient.id)}
                                    aria-label={`Remove ${ingredient.name || "ingredient"}`}
                                    className="px-2 text-red-700"
                                >
                                    ✕
                                </button>
                            </div>
                        </div>
                    ))}

                    {recipe.ingredients.length === 0 && (
                        <p className="text-sm text-[#536A55]">
                            No ingredients yet. Add your first ingredient.
                        </p>
                    )}
                </section>

                <section className="space-y-4">
                    <div className="flex items-center justify-between">
                        <h2 className="text-xl font-semibold">Instructions</h2>
                        <button
                            type="button"
                            onClick={() =>
                                updateRecipe("instructions", [
                                    ...recipe.instructions,
                                    "",
                                ])
                            }
                            className="font-medium text-[#536A55]"
                        >
                            + Add Step
                        </button>
                    </div>

                    {recipe.instructions.map((step, index) => (
                        <div key={index} className="flex items-start gap-3">
              <span className="pt-3 font-semibold text-[#536A55]">
                {index + 1}.
              </span>

                            <textarea
                                value={step}
                                onChange={(event) =>
                                    updateInstruction(index, event.target.value)
                                }
                                placeholder="Describe this step..."
                                rows={3}
                                className="min-w-0 flex-1 rounded-xl border border-[#536A55]/20 bg-white p-3"
                            />

                            <button
                                type="button"
                                onClick={() => removeInstruction(index)}
                                aria-label={`Remove step ${index + 1}`}
                                className="pt-3 text-red-700"
                            >
                                ✕
                            </button>
                        </div>
                    ))}
                </section>

                {error && (
                    <p role="alert" className="text-sm text-red-700">
                        {error}
                    </p>
                )}

                <button
                    type="button"
                    onClick={handleSave}
                    disabled={saving}
                    className="w-full rounded-xl bg-[#536A55] p-4 font-semibold text-white disabled:opacity-50"
                >
                    {saving ? "Saving..." : "Save to Kitchen"}
                </button>
            </div>
        </main>
    );
}
