import { useState, type ReactNode } from 'react'
import Sidebar from './Sidebar'

type AppLayoutProps = {
    children: ReactNode
}

export default function AppLayout({ children }: AppLayoutProps) {
    const [sidebarOpen, setSidebarOpen] = useState(true)

    return (
        <div className="flex min-h-dvh bg-canvas text-ink">
            {sidebarOpen && (
                <div className="relative shrink-0">
                    <Sidebar />

                    <button
                        type="button"
                        onClick={() => setSidebarOpen(false)}
                        aria-label="Close navigation"
                        className="absolute right-3 top-4 rounded-lg p-2 transition-colors hover:bg-canvas"
                    >
                        ☰
                    </button>
                </div>
            )}

            <main className="min-w-0 flex-1">
                <header className="flex h-16 items-center px-4">
                    {!sidebarOpen && (
                        <button
                            type="button"
                            onClick={() => setSidebarOpen(true)}
                            aria-label="Open navigation"
                            className="rounded-lg p-2 transition-colors hover:bg-surface"
                        >
                            ☰
                        </button>
                    )}
                </header>

                {children}
            </main>
        </div>
    )
}