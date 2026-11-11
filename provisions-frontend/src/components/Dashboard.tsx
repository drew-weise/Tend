import { useUser } from '../hooks/useUser'

export default function Dashboard() {
    const { user } = useUser()

    return <h1>Welcome to Tend</h1>
}