export type AuthUser = {
  id: string;
  email: string;
  displayName: string;
  createdAt: string;
};

export type AuthSession = {
  accessToken: string;
  expiresAt: string;
  user: AuthUser;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type RegisterRequest = LoginRequest & {
  displayName: string;
};
