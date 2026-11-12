
import { useCallback } from 'react'
import { useAuth } from 'react-oidc-context'

const API_BASE_URL = 'http://localhost:8080'

export function useApi() {
    const auth = useAuth()
    const accessToken = auth.user?.access_token

    const apiFetch = useCallback(async (
        endpoint: string,
        options: RequestInit = {}
    ): Promise<Response> => {

        if (!accessToken) {
            throw new Error('User is not authenticated')
        }

        const headers = new Headers(options.headers)
        headers.set('Authorization', `Bearer ${accessToken}`)

        const response = await fetch(
            `${API_BASE_URL}${endpoint}`,
            {
                ...options,
                headers
            }
        )

        if (!response.ok) {
            throw new Error(
                `API request failed: ${response.status}`
            )
        }

        return response

    }, [accessToken])

    return { apiFetch }
}
