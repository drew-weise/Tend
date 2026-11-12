
import {
    Sprout,
    ChevronLeft,
    Menu,
    MessageCircle,
    BookOpen
} from 'lucide-react'
import { NavLink } from 'react-router-dom'

type SidebarProps = {
    isOpen: boolean
    onToggle: () => void
}

const navigation = [
    { label: 'Chat', path: '/chat', icon: MessageCircle },
    { label: 'Kitchen', path: '/kitchen', icon: BookOpen }
]

export default function Sidebar({ isOpen, onToggle }: SidebarProps) {
    return (
        <>
            {/* Open navigation button */}
            {!isOpen && (
                <button
                    type="button"
                    onClick={onToggle}
                    aria-label="Open navigation"
                    className="fixed left-4 top-4 z-50 rounded-lg p-2 text-primary transition-colors hover:bg-surface"
                >
                    <Menu size={22} strokeWidth={1.5} />
                </button>
            )}

            {/* Backdrop */}
            {isOpen && (
                <button
                    type="button"
                    onClick={onToggle}
                    aria-label="Close navigation"
                    className="fixed inset-0 z-40 cursor-default bg-ink/10"
                />
            )}

            {/* Sidebar */}
            <aside
                className={`
                    fixed inset-y-0 left-0 z-50
                    w-52 rounded-r-2xl bg-[#EEE9DF]/70 p-5
                    backdrop-blur-xl
                    transition-transform duration-300 ease-in-out
                    ${isOpen ? 'translate-x-0' : '-translate-x-full'}
                `}
            >
                {/* Header */}
                <div className="mb-10 flex items-center justify-between">
                    <div className="flex h-9 w-9 items-center justify-center rounded-full bg-accent/20">
                        <Sprout
                            size={19}
                            strokeWidth={1.5}
                            className="text-primary"
                        />
                    </div>

                    <button
                        type="button"
                        onClick={onToggle}
                        aria-label="Close navigation"
                        className="rounded-lg p-2 text-primary transition-colors hover:bg-canvas"
                    >
                        <ChevronLeft size={22} strokeWidth={1.5} />
                    </button>
                </div>

                {/* Navigation */}
                <nav aria-label="Main navigation" className="space-y-1">
                    {navigation.map(({ label, path, icon: Icon }) => (
                        <NavLink
                            key={path}
                            to={path}
                            onClick={() => {
                                if (isOpen) onToggle()
                            }}
                            className={({ isActive }) => `
                                flex items-center gap-3 rounded-xl px-3 py-3
                                text-sm transition-colors
                                ${isActive
                                ? 'bg-accent/25 font-medium text-primary'
                                : 'text-ink/70 hover:bg-accent/10 hover:text-primary'
                            }
                            `}
                        >
                            <Icon size={19} strokeWidth={1.6} />
                            <span>{label}</span>
                        </NavLink>
                    ))}
                </nav>
            </aside>
        </>
    )
}
