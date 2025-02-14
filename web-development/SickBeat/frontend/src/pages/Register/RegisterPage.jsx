import React from "react";
import styles from "./RegisterPage.module.css";
import { useField } from "../../hooks/useField";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { Link } from "react-router-dom";
import { useBackend } from "../../hooks/useBackend";
import Cookies from "js-cookie";

const RegisterPage = ({ setIsAuthenticated }) => {
	const emailField = useField("email");
	const usernameField = useField("text");
	const passwordField = useField("password");
	const confirmPasswordField = useField("password");

	const { sendRequest } = useBackend();
	const Navigate = useNavigate();

	const handleRegister = async (event) => {
		event.preventDefault();
		try {
			if (
				emailField.value &&
				usernameField.value &&
				passwordField.value &&
				confirmPasswordField.value
			) {
				if (passwordField.value !== confirmPasswordField.value) {
					toast.error("Passwords do not match");
				}

				const response = await sendRequest(
					"/api/user/register",
					"POST",
					{
						email: emailField.value,
						username: usernameField.value,
						password: passwordField.value,
					}
				);

				if (response.statusText === "OK") {
					Cookies.set("jwt", response.data.token, { expires: 1 });
					setIsAuthenticated(true);
					Navigate("/", {
						state: { message: "Registration successful" },
					});
				} else {
					toast.error(response.message || "Registration failed");
				}
			} else {
				toast.error("Please fill in all fields");
			}
		} catch (e) {
			console.error("Error:", e);
		}
	};

	return (
		<div className={`${styles.registerContainer}`}>
			<div className={styles.registerBox}>
				<h2 className={styles.registerTitle}>Register</h2>
				<form onSubmit={handleRegister}>
					<div className={styles.inputGroup}>
						<input
							type={emailField.type}
							placeholder='EMAIL'
							value={emailField.value.trim()}
							onChange={emailField.onChange}
							className={styles.inputField}
							autoComplete='current-email'
							required={true}
						/>
					</div>
					<div className={styles.inputGroup}>
						<input
							type={usernameField.type}
							placeholder='USERNAME'
							value={usernameField.value.trim()}
							onChange={usernameField.onChange}
							className={styles.inputField}
							autoComplete='current-username'
							required={true}
						/>
					</div>
					<div className={styles.inputGroup}>
						<input
							type={passwordField.type}
							placeholder='PASSWORD'
							value={passwordField.value.trim()}
							onChange={passwordField.onChange}
							className={styles.inputField}
							autoComplete='current-password'
							required={true}
						/>
					</div>
					<div className={styles.inputGroup}>
						<input
							type={confirmPasswordField.type}
							placeholder='CONFIRM PASSWORD'
							value={confirmPasswordField.value.trim()}
							onChange={confirmPasswordField.onChange}
							className={styles.inputField}
							autoComplete='current-password'
							required={true}
						/>
					</div>
					<button type='submit' className={styles.submitButton}>
						REGISTER →
					</button>
				</form>
				<div className={styles.linkContainer}>
					<p>
						Already have an account?{" "}
						<Link to='/login' className={styles.link}>
							Click here to Log in
						</Link>
					</p>
				</div>
			</div>
		</div>
	);
};

export default RegisterPage;
