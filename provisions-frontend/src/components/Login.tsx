import { useEffect, useState } from 'react'
import { useAuth } from 'react-oidc-context'

type DatabaseUser = {
    userId: number
    cognitoUserId: string
    userEmail: string
}

export default function Login() {
    const auth = useAuth()
    const accessToken = auth.user?.access_token

    const [user, setUser] = useState<DatabaseUser | null>(null)
    const [userError, setUserError] = useState<string | null>(null)

    useEffect(() => {
        if (!auth.isAuthenticated || !accessToken) {
            setUser(null)
            setUserError(null)
            return
        }

        const controller = new AbortController()

        setUser(null)
        setUserError(null)

        async function loadUser() {
            try {
                const response = await fetch(
                    'http://localhost:8080/api/users/me',
                    {
                        headers: {Authorization: `Bearer ${accessToken}`,},
                        signal: controller.signal,
                    }
                )

                if (!response.ok) {
                    throw new Error(`Could not load user: ${response.status}`)
                }

                const databaseUser: DatabaseUser = await response.json()
                setUser(databaseUser)
            } catch (error) {
                if (controller.signal.aborted) return

                setUserError(error instanceof Error ? error.message : 'Could not load user')
            }
        }

        loadUser()

        return () => controller.abort()
    }, [auth.isAuthenticated, accessToken])

    if (auth.isLoading) {
        return <div>Loading...</div>
    }

    if (auth.error) {
        return <div>Login error: {auth.error.message}</div>
    }

    if (auth.isAuthenticated) {
        if (userError) {
            return <div>{userError}</div>
        }

        if (!user) {
            return <div>Loading your account...</div>
        }

        return (
            <div>
                <p>Signed in as {auth.user?.profile.email}</p>
                <p>Database user ID: {user.userId}</p>
            </div>
        )
    }

    return (
        <button onClick={() => auth.signinRedirect()}>
            Sign in
        </button>
    )
}