import { useState, useEffect } from 'react'
import { useAuth } from 'react-oidc-context'
import type { ReactNode } from 'react'
import type { DatabaseUser } from '../types/DatabaseUser'
import { UserContext } from './UserContext'


export function UserProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<DatabaseUser | null>(null)
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const auth = useAuth()
    const accessToken = auth.user?.access_token

    useEffect(() => {
        if (!auth.isAuthenticated || !accessToken) {
            setUser(null)
            setError(null)
            setIsLoading(false)
            return
        }

        const controller = new AbortController()

        setUser(null)
        setError(null)
        setIsLoading(true)

        async function loadUser() {
            try {
                const response = await fetch(
                    'http://localhost:8080/api/users/me',
                    {
                        headers: {Authorization: `Bearer ${accessToken}`},
                        signal: controller.signal
                    }
                )

                if (!response.ok) {
                    throw new Error(`Could not load user: ${response.status}`)
                }

                const databaseUser: DatabaseUser = await response.json()

                if (!controller.signal.aborted) {
                    setUser(databaseUser)
                }
            } catch (err) {
                if (controller.signal.aborted) return

                setError(err instanceof Error ? err.message : 'Could not load user')
            } finally {
                if (!controller.signal.aborted) {
                    setIsLoading(false)
                }
            }
        }

        loadUser()

        return () => controller.abort()
    }, [auth.isAuthenticated, accessToken])


    return (
        <UserContext.Provider value={{ user, isLoading, error }}>
            {children}
        </UserContext.Provider>
    )
}