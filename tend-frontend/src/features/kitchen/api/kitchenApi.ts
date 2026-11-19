
import type { KitchenItem } from '../../../types/KitchenItem'

export async function getKitchenItems(
    apiFetch: (
        endpoint: string,
        options?: RequestInit
    ) => Promise<Response>,
    signal?: AbortSignal
): Promise<KitchenItem[]> {

    const response = await apiFetch('/api/recipe', {
        signal
    })

    return response.json()
}
