import { createContext } from 'react'
import type { DatabaseUser } from '../types/DatabaseUser'

export type UserContextValue = {
    user: DatabaseUser | null
    isLoading: boolean
    error: string | null
}

export const UserContext =
    createContext<UserContextValue | undefined>(undefined)