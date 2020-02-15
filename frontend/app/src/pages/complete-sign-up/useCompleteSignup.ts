import { useFormik } from 'formik';
import * as Yup from 'yup';
import { passwordSchema } from 'validation/password';
import { completeSignup, SignupRequestDTO } from 'api';

const CompleteSignupSchema = Yup.object().shape({
  password: passwordSchema,
});

const useCompleteSignup = (signupRequest: SignupRequestDTO) => {
  const formik = useFormik({
    initialValues: {
      password: '',
    },
    onSubmit: values => {
      completeSignup({ ...signupRequest, password: values.password })
        .then(response => {
          console.log(response);
        })
        .catch(error => {
          console.error(error);
        });
    },
    validationSchema: CompleteSignupSchema,
  });

  return formik;
};

export default useCompleteSignup;
