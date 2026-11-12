import AuthGuard from './components/AuthGuard'
import Dashboard from './components/Dashboard'
import AppLayout from './components/layout/AppLayout'

function App() {
    return (
        <AuthGuard>
            <AppLayout>
                <Dashboard />
            </AppLayout>
        </AuthGuard>
    )
}

export default App