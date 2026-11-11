import { useContext } from 'react'
import { UserContext } from '../contexts/UserContext'

export function useUser() {
    const context = useContext(UserContext)

    if (context === undefined) {
        throw new Error('useUser must be used inside UserProvider')
    }

    return context
}