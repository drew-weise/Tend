import { useEffect } from 'react'
import type { ReactNode } from 'react'
import { useAuth } from 'react-oidc-context'
import { useUser } from '../hooks/useUser'

export default function AuthGuard({ children }: { children: ReactNode }) {
    const auth = useAuth()
    const { user, isLoading, error } = useUser()

    useEffect(() => {
        if (
            !auth.isLoading &&
            !auth.isAuthenticated &&
            !auth.activeNavigator &&
            !auth.error
        ) {
            void auth.signinRedirect()
        }
    }, [
        auth.isLoading,
        auth.isAuthenticated,
        auth.activeNavigator,
        auth.error,
        auth.signinRedirect
    ])

    if (auth.error) {
        return <p>Authentication error: {auth.error.message}</p>
    }

    if (auth.isLoading || !auth.isAuthenticated) {
        return <p>Checking authentication...</p>
    }

    if (error) {
        return <p>Failed to load user: {error}</p>
    }

    if (isLoading || !user) {
        return <p>Loading your account...</p>
    }

    return <>{children}</>
}