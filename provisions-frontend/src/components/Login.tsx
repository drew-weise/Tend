import { useAuth } from 'react-oidc-context'

export default function Login() {
    const auth = useAuth()

    if (auth.isLoading) {
        return <div>Loading...</div>
    }

    if (auth.error) {
        return <div>Login error: {auth.error.message}</div>
    }

    if (auth.isAuthenticated) {
        return <div>Signed in as {auth.user?.profile.email}</div>
    }

    return (
        <button onClick={() => auth.signinRedirect()}>
            Sign in
        </button>
    )
}