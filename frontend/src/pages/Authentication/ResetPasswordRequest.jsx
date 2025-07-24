import React from "react";
import { Formik, Field, Form, ErrorMessage } from "formik";
import * as Yup from "yup";
import {
  TextField,
  Button,
  Grid,
  RadioGroup,
  FormControlLabel,
  Radio,
} from "@mui/material";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { loginUser, resetPasswordRequest } from "../../Redux/Auth/auth.action";

const validationSchema = Yup.object().shape({

  email: Yup.string().email("Invalid email").required("Email is required"),
  
});

const theme = createTheme();

function ResetPasswordRequest() {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const initialValues = {
    email: "",
  };

  const handleSubmit = (values, { setSubmitting }) => {
    console.log(values);
    dispatch(resetPasswordRequest(values.email));
    setSubmitting(false);
  };

  return (
    <>
      <Formik
        initialValues={initialValues}
        validationSchema={validationSchema}
        onSubmit={handleSubmit}
      >
        <Form className="space-y-5">
          <div className="space-y-3">
            <div>
              <Field
                as={TextField}
                name="email"
                placeholder="Email"
                type="email"
                variant="outlined"
                fullWidth
              />
              <ErrorMessage
                name="email"
                component="div"
                className="text-red-500"
              />
            </div>

          </div>          <Button  sx={{ padding: ".8rem 0rem" }} fullWidth type="submit" variant="contained" color="primary">
            Send Reset Link
          </Button>
        </Form>
      </Formik>
      
      <div className="flex items-center space-x-4 mt-5 justify-center">
        <button 
          onClick={() => navigate("/login")}
          className="text-blue-400 hover:text-blue-300 underline bg-transparent border-none cursor-pointer transition-colors duration-200 font-medium"
        >
          LOGIN
        </button>
        <span className="text-gray-400">|</span>
        <button 
          onClick={() => navigate("/register")}
          className="text-blue-400 hover:text-blue-300 underline bg-transparent border-none cursor-pointer transition-colors duration-200 font-medium"
        >
          REGISTER
        </button>
      </div></>
  );
}

export default ResetPasswordRequest;
