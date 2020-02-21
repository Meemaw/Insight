import ky from 'ky-universal';

const baseURL = 'http://localhost:8080';

export type DataResponse<T> = {
  data: T;
};

export const SsoApi = {
  login: (email: string, password: string) => {
    const body = new URLSearchParams();
    body.set('email', email);
    body.set('password', password);
    return ky
      .post(`${baseURL}/v1/sso/login`, { body, credentials: 'include' })
      .json();
  },
};

export type PasswordResetRequestBase = {
  email: string;
  token: string;
  org: string;
};

export type PasswordResetRequestDTO = PasswordResetRequestBase & {
  password: string;
};

export const PasswordApi = {
  forgot: (email: string) => {
    return ky
      .post(`${baseURL}/v1/password/forgot`, { json: { email } })
      .json<DataResponse<boolean>>();
  },
  reset: (json: PasswordResetRequestDTO) => {
    return ky
      .post(`${baseURL}/v1/password/reset`, { json })
      .json<DataResponse<boolean>>();
  },
};

export type SignupRequestDTO = {
  email: string;
  token: string;
  org: string;
};

export const verifySignup = (json: SignupRequestDTO) => {
  const url = `${baseURL}/v1/signup/verify`;
  return ky.post(url, { json }).json<DataResponse<boolean>>();
};

type CompleteSignupRequestDTO = SignupRequestDTO & {
  password: string;
};

export const completeSignup = (json: CompleteSignupRequestDTO) => {
  return ky
    .post(`${baseURL}/v1/signup/complete`, { json })
    .json<DataResponse<boolean>>();
};

export type UserDTO = {
  id: string;
  email: string;
  role: string;
};

export const session = (sessionId: string) => {
  return ky.get(`${baseURL}/v1/sso/session`, {
    searchParams: { id: sessionId },
  });
};

export const me = () => {
  return ky.get(`${baseURL}/v1/sso/me`, { credentials: 'include' });
};

export const logout = () => {
  return ky.post(`${baseURL}/v1/sso/logout`, { credentials: 'include' });
};

export type UserRole = 'ADMIN' | 'STANDARD';

export type TeamInvite = {
  token: string;
  email: string;
  org: string;
  creator: string;
  role: UserRole;
  createdAt: number;
};

export const InviteApi = {
  list: () => {
    return ky
      .get(`${baseURL}/v1/org/invites`, { credentials: 'include' })
      .json<DataResponse<TeamInvite[]>>();
  },
  delete: (token: string, email: string) => {
    return ky
      .delete(`${baseURL}/v1/org/invites/${token}`, {
        json: { email },
        credentials: 'include',
      })
      .json<DataResponse<boolean>>();
  },
  create: (role: UserRole, email: string) => {
    return ky
      .post(`${baseURL}/v1/org/invites`, {
        json: { email, role },
        credentials: 'include',
      })
      .json<DataResponse<TeamInvite>>();
  },
  resend: (email: string) => {
    return ky
      .post(`${baseURL}/v1/org/invites/send`, {
        json: { email },
        credentials: 'include',
      })
      .json<DataResponse<boolean>>();
  },
};

export type APIErrorDataResponse = {
  error: APIError;
};

export type APIError = {
  statusCode: number;
  reason: string;
  message: string;
};
