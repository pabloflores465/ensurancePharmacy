export const useProfile = () => useState('profile', (): {} => {
    if (import.meta.client) {
        const profile = localStorage.getItem('profile')
        if (profile) {
            return JSON.parse(profile)
        }
    }
    return {}
})

export const setProfile = (profile: object) => {
    if (import.meta.client) {
        localStorage.setItem('profile', JSON.stringify(profile))
    }
}
