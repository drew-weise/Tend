import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import AuthGuard from './AuthGuard.tsx'
import AppLayout from './AppLayout.tsx'
import ChatPage from '../pages/ChatPage.tsx'
import KitchenPage from '../pages/KitchenPage.tsx'

function App() {
    return (
        <BrowserRouter>
            <AuthGuard>
                <AppLayout>
                    <Routes>
                        <Route path="/" element={<Navigate to="/chat" replace />} />
                        <Route path="/chat" element={<ChatPage />} />
                        <Route path="/kitchen" element={<KitchenPage />} />
                    </Routes>
                </AppLayout>
            </AuthGuard>
        </BrowserRouter>
    )
}

export default App
