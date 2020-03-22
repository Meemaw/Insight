import { useFormik } from 'formik';
import * as Yup from 'yup';
import { passwordSchema } from 'modules/auth/validation/password';
import SignupApi, { SignupRequestDTO } from 'api/signup';
import { APIErrorDataResponse } from 'api';
import { useState } from 'react';
import { useRouter } from 'next/router';

const CompleteSignupSchema = Yup.object().shape({
  password: passwordSchema,
});

const useCompleteSignup = (signupRequest: SignupRequestDTO) => {
  const router = useRouter();
  const [formError, setFormError] = useState<string | undefined>();

  const formik = useFormik({
    initialValues: {
      password: '',
    },
    onSubmit: (values, { setSubmitting }) => {
      SignupApi.complete({ ...signupRequest, password: values.password })
        .then((_) => router.replace('/'))
        .catch(async (error) => {
          const errorDTO: APIErrorDataResponse = await error.response.json();
          setFormError(errorDTO.error.message);
        })
        .finally(() => setSubmitting(false));
    },
    validationSchema: CompleteSignupSchema,
  });

  return { ...formik, formError };
};

export default useCompleteSignup;
