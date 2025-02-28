import os from 'os';

export const useIP = () => {
    let ip = '127.0.0.1';
    if (!import.meta.client) {
        const networkInterfaces = os.networkInterfaces();
        ip = networkInterfaces['eth0']?.[0]?.address || '127.0.0.1';
    }    
    return ip;
}

