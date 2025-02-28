// composable: useProfile.ts
import { useState } from "#app";

export interface User {
    idUser: number;
    name: string;
    cui: number;
    phone: string;
    email: string;
    address: string;
    birthDate: number;
    role: string;
    policy: object;
    enabled: number;
    password: string;
}

export const useProfile = () =>
    useState<User | null>('profile', (): User | null => {
        if (import.meta.client) {
            const profile = localStorage.getItem('profile');
            if (profile) {
                return JSON.parse(profile) as User;
            }
        }
        return null;
    });

export const setProfile = (newProfile: User) => {
    if (import.meta.client) {
        localStorage.setItem('profile', JSON.stringify(newProfile));
        const profileState = useProfile();
        profileState.value = newProfile;
    }
};
