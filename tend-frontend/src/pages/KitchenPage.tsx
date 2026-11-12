
import { Plus, Search } from 'lucide-react'
import KitchenHeader from '../features/kitchen/components/KitchenHeader.tsx'

export default function KitchenPage() {
    return (
        <div className="min-h-dvh bg-canvas px-6 pb-12 pt-24">
            <div className="mx-auto w-full max-w-4xl">
                <KitchenHeader />

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

                    <div className="rounded-2xl border border-primary/10 bg-white/40 px-6 py-16 text-center">
                        <p className="font-serif text-xl text-ink">
                            Your kitchen is waiting.
                        </p>

                        <p className="mt-3 text-sm text-primary">
                            Your favorite foods and meals will find a home here.
                        </p>
                    </div>
                </section>
            </div>
        </div>
    )
}
