import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { AuthProvider } from 'react-oidc-context'
import { UserProvider } from './contexts/UserProvider'
import './index.css'
import App from './app/App.tsx'

const cognitoAuthConfig = {
    authority: 'https://cognito-idp.us-east-2.amazonaws.com/us-east-2_rCmUkdvgz',
    client_id: '2flp35c0kdfadenj5hgh07225a',
    redirect_uri: 'http://localhost:5173',
    response_type: 'code',
    scope: 'email openid',
    onSigninCallback: () => {
        window.history.replaceState({}, document.title, window.location.pathname)
    },
}

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <AuthProvider {...cognitoAuthConfig}>
            <UserProvider>
                <App />
            </UserProvider>
        </AuthProvider>
    </StrictMode>,
)