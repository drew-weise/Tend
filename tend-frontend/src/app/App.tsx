import AuthGuard from './AuthGuard.tsx'
import AppLayout from './AppLayout.tsx'
import ChatPage from '../pages/ChatPage.tsx'

function App() {
    return (
        <AuthGuard>
            <AppLayout>
                <ChatPage></ChatPage>
            </AppLayout>
        </AuthGuard>
    )
}

export default App