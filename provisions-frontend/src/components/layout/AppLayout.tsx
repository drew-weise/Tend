import type { ReactNode } from 'react'

type AppLayoutProps = {
    children: ReactNode
}

export default function AppLayout({ children }: AppLayoutProps) {
    return (
        <div className="min-h-dvh bg-stone-50 text-stone-900">
            <main>{children}</main>
        </div>
    )
}