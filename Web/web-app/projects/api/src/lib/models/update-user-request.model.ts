export interface UpdateUserRequest {
  login: string;
  name: string;
  email: string;
  userId: number;
  rawPassword: string;
}
