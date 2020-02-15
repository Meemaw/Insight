import ky from 'ky-universal';

const baseURL = 'http://localhost:8080';

export type DataResponse<T> = {
  data: T;
};

export const login = (email: string, password: string) => {
  const body = new URLSearchParams();
  body.set('email', email);
  body.set('password', password);
  return ky
    .post(`${baseURL}/v1/sso/login`, { body, credentials: 'include' })
    .json();
};

export type SignupRequestDTO = {
  email: string;
  token: string;
  org: string;
};

export const verifySignup = (signupRequest: SignupRequestDTO) => {
  const url = `${baseURL}/v1/signup/verify`;
  return ky.post(url, { json: signupRequest }).json<DataResponse<boolean>>();
};

type CompleteSignupRequestDTO = SignupRequestDTO & {
  password: string;
};

export const completeSignup = (
  completeSignupRequest: CompleteSignupRequestDTO
) => {
  return ky
    .post(`${baseURL}/v1/signup/complete`, { json: completeSignupRequest })
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
