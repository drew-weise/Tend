import forest from '../assets/landscapes/forest.png'
import vine from '../assets/landscapes/vine.png'

export default function ChatPage() {
    return (
        <div className="relative flex min-h-dvh flex-col items-center justify-center overflow-hidden bg-canvas px-6">

            {/* Forest background */}


            <img
                src={forest}
                alt=""
                aria-hidden="true"
                className="
                    pointer-events-none absolute bottom-0 left-0
                    h-[45%] w-full object-cover object-bottom
                    [mask-image:linear-gradient(to_bottom,transparent,black_35%)]
                    md:h-auto md:w-full
                "
            />



            {/* Decorative vine */}
            <img
                src={vine}
                alt=""
                aria-hidden="true"
                className="pointer-events-none absolute right-0 top-0 w-72"
            />

            {/* Chat welcome content */}
            <div className="relative z-10 w-full max-w-xl">
                <div className="mb-10 text-center">
                    <h1 className="font-serif text-4xl font-normal tracking-tight text-ink">
                        Good morning.
                    </h1>

                    <p className="mt-3 text-base text-primary">
                        What's on your mind today?
                    </p>
                </div>

                {/* Message composer */}
                <div className="flex items-center gap-3 rounded-3xl bg-white/75 px-5 py-3 shadow-sm">
                    <input
                        type="text"
                        placeholder="Ask Tend anything..."
                        className="min-w-0 flex-1 bg-transparent py-2 text-ink outline-none placeholder:text-ink/40"
                    />

                    <button
                        type="button"
                        aria-label="Send message"
                        className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-primary text-white transition-colors hover:bg-ink"
                    >
                        ↑
                    </button>
                </div>
            </div>
        </div>
    )
}
