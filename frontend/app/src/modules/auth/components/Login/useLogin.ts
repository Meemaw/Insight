import { SsoApi, APIErrorDataResponse } from 'api';
import { useFormik } from 'formik';
import { emailSchema } from 'validation/email';
import { passwordSchema } from 'validation/password';
import * as Yup from 'yup';
import { useState } from 'react';
import { useRouter } from 'next/router';

const LoginSchema = Yup.object().shape({
  email: emailSchema,
  password: passwordSchema,
  rememberMe: Yup.boolean().required(),
});

const useLogin = () => {
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
        .then(_ => router.replace('/'))
        .catch(async error => {
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
