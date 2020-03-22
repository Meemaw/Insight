import SsoApi from 'api/sso';
import { useFormik } from 'formik';
import { emailSchema } from 'modules/auth/validation/email';
import { passwordSchema } from 'modules/auth/validation/password';
import * as Yup from 'yup';
import { useState } from 'react';
import { useRouter } from 'next/router';
import { APIErrorDataResponse } from 'api';

const LoginSchema = Yup.object().shape({
  email: emailSchema,
  password: passwordSchema,
  rememberMe: Yup.boolean().required(),
});

const useLogin = (dest: string) => {
  const router = useRouter();
  const [formError, setFormError] = useState<string | undefined>();

  const formik = useFormik({
    initialValues: {
      email: '',
      password: '',
      rememberMe: false,
    },
    onSubmit: (values, { setSubmitting }) => {
      setFormError(undefined);
      SsoApi.login(values.email, values.password)
        .then((_) => router.replace(decodeURIComponent(dest)))
        .catch(async (error) => {
          const errorDTO: APIErrorDataResponse = await error.response.json();
          setFormError(errorDTO.error.message);
        })
        .finally(() => setSubmitting(false));
    },
    validationSchema: LoginSchema,
  });

  return { ...formik, formError };
};

export default useLogin;
