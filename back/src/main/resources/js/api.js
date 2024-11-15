import { API_BASE_URL } from './config';

export const fetchTags = async (accessToken) => {
    try {
        const response = await fetch(`${API_BASE_URL}/api/v1/tags`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${accessToken}`,
                'Content-Type': 'application/json',
            },
        });
        const data = await response.json();
        if (!data.isSuccess) {
            throw new Error(data.message);
        }
        return data.result;
    } catch (error) {
        console.error('Error fetching tags:', error);
        throw error;
    }
};
