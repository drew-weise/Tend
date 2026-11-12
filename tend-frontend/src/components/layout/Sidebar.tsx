
type SidebarProps = {
    isOpen: boolean
    onToggle: () => void
}

export default function Sidebar({ isOpen, onToggle }: SidebarProps) {
    return (
        <>
            {/* Open navigation button */}
            {!isOpen && (
                <button
                    type="button"
                    onClick={onToggle}
                    aria-label="Open navigation"
                    className="fixed left-4 top-4 z-50 rounded-lg p-2 transition-colors hover:bg-surface"
                >
                    ☰
                </button>
            )}

            {/* Backdrop */}
            {isOpen && (
                <button
                    type="button"
                    onClick={onToggle}
                    aria-label="Close navigation"
                    className="fixed inset-0 z-40 cursor-default bg-ink/30"
                />
            )}

            {/* Sidebar */}
            <aside
                className={`
                    fixed inset-y-0 left-0 z-50
                    w-64 bg-surface p-6
                    transition-transform duration-300 ease-in-out
                    ${isOpen ? 'translate-x-0' : '-translate-x-full'}
                `}
            >
                <h1 className="text-2xl font-semibold tracking-tight">
                    tend.
                </h1>

                <button
                    type="button"
                    onClick={onToggle}
                    aria-label="Close navigation"
                    className="absolute right-3 top-4 rounded-lg p-2 hover:bg-canvas"
                >
                    ☰
                </button>
            </aside>
        </>
    )
}
