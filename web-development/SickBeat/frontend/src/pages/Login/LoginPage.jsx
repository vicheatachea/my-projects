import React from "react";
import styles from "./LoginPage.module.css"; // Import the CSS file
import { useBackend } from "../../hooks/useBackend";
import { useField } from "../../hooks/useField";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";
import Cookies from "js-cookie";

const LoginPage = ({ setIsAuthenticated }) => {
	const emailField = useField("email");
	const passwordField = useField("password");
	const navigate = useNavigate();
	const { sendRequest } = useBackend();

	const handleLogin = async (event) => {
		event.preventDefault(); // Prevent default form submission behavior

		try {
			if (emailField.value && passwordField.value) {
				const response = await sendRequest("/api/user/login", "POST", {
					email: emailField.value,
					password: passwordField.value,
				});
               
                if (response.statusText === "OK") {
                    Cookies.set("jwt", response.data.token, { expires: 1 });
					setIsAuthenticated(true);
					navigate("/", { state: { message: "Login successful" } });
				} else {
					toast.error(response.message || "Login failed");
				}
			} else {
				toast.error("Please fill in all fields");
			}
		} catch (e) {
			console.error("Error:", e);
		}
	};

	return (
		<div className={`${styles.loginContainer}`}>
			<div className={styles.loginBox}>
				<h2 className={styles.loginTitle}>Login</h2>
				<form onSubmit={handleLogin}>
					<input
						type={emailField.type}
						placeholder='EMAIL'
						value={emailField.value.replace(/\s/g, "")}
						onChange={emailField.onChange}
						className={styles.inputField}
						autoComplete='current-email'
						required={true}
					/>
					<input
						type={passwordField.type}
						placeholder='PASSWORD'
						value={passwordField.value.replace(/\s/g, "")}
						onChange={passwordField.onChange}
						className={styles.inputField}
						autoComplete='current-password'
						required={true}
					/>
					<button type='submit' className={styles.submitButton}>
						LOG IN →
					</button>
				</form>
				<div className={styles.linkContainer}>
					<p>
						Don't have an account already?{" "}
						<Link to='/register' className={styles.link}>
							Click here to sign up
						</Link>
					</p>
					<p>
						<Link to='/forgot-password' className={styles.link}>
							Forgot your password?
						</Link>
					</p>
				</div>
			</div>
		</div>
	);
};

export default LoginPage;
