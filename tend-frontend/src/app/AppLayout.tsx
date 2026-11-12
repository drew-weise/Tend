
import { useState, type ReactNode } from 'react'
import Sidebar from '../components/layout/Sidebar.tsx'

type AppLayoutProps = {
    children: ReactNode
}

export default function AppLayout({ children }: AppLayoutProps) {
    const [sidebarOpen, setSidebarOpen] = useState(true)

    return (
        <div className="flex min-h-dvh bg-canvas text-ink">
            <Sidebar
                isOpen={sidebarOpen}
                onToggle={() => setSidebarOpen(!sidebarOpen)}
            />

            <main className="min-w-0 flex-1">
                {children}
            </main>
        </div>
    )
}
