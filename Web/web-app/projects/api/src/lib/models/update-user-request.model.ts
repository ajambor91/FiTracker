export interface UpdateUserRequest {
  login: string;
  name: string | null;
  email: string | null;
  userId: number;
  rawPassword: string;
}
