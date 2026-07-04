export interface User {
  id: number;
  email: string;
  username: string;
  firstName: string;
  lastName: string;
  phone: string;
  role: string;
  fullName: string;
}

export interface LoginRequest {
  username: string;
  password: string;
  deviceFingerprint?: string;
  deviceName?: string;
  ipAddress?: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  username: string;
  password: string;
  phone?: string;
  role?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  username: string;
  role: string;
  fullName: string;
  userId: number;
}
