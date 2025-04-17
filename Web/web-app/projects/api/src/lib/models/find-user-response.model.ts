export interface FindUser {
  id: number;
  name: string;
  email: string;
}

export interface FindUserResponse {
  userData: FindUser[];
}
