import React, { useEffect, useState } from "react";
import styles from "./AccountPage.module.css";
import profilePicture from "../../images/the-rock.jpg";
import { useBackend } from "../../hooks/useBackend";
import Cookies from "js-cookie";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { useField } from "../../hooks/useField";

const AccountPage = ({ theme, setIsAuthenticated }) => {
	const usernameField = useField("text");
	const emailField = useField("email");
	const newPasswordField = useField("password");
	const confirmPasswordField = useField("password");

	const [initialUsername, setInitialUsername] = useState("");
	const [initialEmail, setInitialEmail] = useState("");
	const [modifiedUsername, setModifiedUsername] = useState(false);
	const [modifiedEmail, setModifiedEmail] = useState(false);
	const [modifiedNewPassword, setModifiedNewPassword] = useState(false);
	const [modifiedConfirmPassword, setModifiedConfirmPassword] =
		useState(false);

	const navigate = useNavigate();
	const { sendRequest } = useBackend();

	const token = Cookies.get("jwt");
	const user = JSON.parse(atob(token.split(".")[1]));
	const userId = user.userId;

	useEffect(() => {
		const getUserData = async () => {
			try {
				const response = await sendRequest(`/api/user/data`, "GET");
				setInitialUsername(response.data.username);
				setInitialEmail(response.data.email);
				usernameField.setValue(response.data.username);
				emailField.setValue(response.data.email);
				resetFields();
				resetModified();
			} catch (error) {
				toast.error("Error fetching user data");
				console.error("Error fetching user data:", error);
			}
		};
		getUserData();
	}, [userId]);

	const resetModified = () => {
		if (modifiedUsername) {
			setModifiedUsername(false);
		}
		if (modifiedEmail) {
			setModifiedEmail(false);
		}
		if (modifiedNewPassword) {
			setModifiedNewPassword(false);
		}
		if (modifiedConfirmPassword) {
			setModifiedConfirmPassword(false);
		}
	};

	const resetFields = () => {
		if (modifiedUsername) {
			usernameField.setValue(initialUsername);
		}
		if (modifiedEmail) {
			emailField.setValue(initialEmail);
		}
		if (modifiedNewPassword) {
			newPasswordField.reset();
		}
		if (modifiedConfirmPassword) {
			confirmPasswordField.reset();
		}
	};

	const handleSaveChanges = async () => {
		const updatedData = { userId: userId };
		if (!window.confirm("Are you sure you want to save changes?")) {
			return;
		}

		if (modifiedNewPassword && modifiedConfirmPassword) {
			if (newPasswordField.value !== confirmPasswordField.value) {
				toast.error("Passwords do not match");
				resetFields();
				resetModified();
				return;
			}

			if (newPasswordField.value.includes(" ")) {
				toast.error("Password cannot contain spaces");
				resetFields();
				resetModified();
				return;
			}

			updatedData.password = newPasswordField.value.trim();
		}
		if (modifiedEmail) {
			if (emailField.value === "") {
				toast.error("Email cannot be empty");
				resetFields();
				resetModified();
				return;
			}

			if (emailField.value.includes(" ")) {
				toast.error("Email cannot contain spaces");
				resetFields();
				resetModified();
				return;
			}

			if (emailField.value === initialEmail) {
				toast.error("Email must be different from current email!");
				resetFields();
				resetModified();
				return;
			}

			updatedData.email = emailField.value.trim();
		}

		if (modifiedUsername) {
			if (usernameField.value === "") {
				toast.error("Username cannot be empty");
				resetFields();
				resetModified();
				return;
			}

			if (usernameField.value.includes(" ")) {
				toast.error("Username cannot contain spaces");
				resetFields();
				resetModified();
				return;
			}

			if (usernameField.value === initialUsername) {
				toast.error(
					"Username must be different from current username!"
				);
				resetFields();
				resetModified();
				return;
			}

			updatedData.username = usernameField.value.trim();
		}
		if (Object.keys(updatedData).length > 1) {
			try {
				const response = await sendRequest(
					"/api/user/update",
					"PATCH",
					updatedData
				);
				if (response.statusText === "OK") {
					setInitialUsername(response.data.username);
					setInitialEmail(response.data.email);
					// resetFields();
					resetModified();
					toast.success("Changes saved successfully");
				} else {
					toast.error(response.message || "Failed to save changes");
					resetFields();
					resetModified();
				}
			} catch (error) {
				toast.error(error.message || "Failed to save changes");
				resetFields();
				resetModified();
			}
		} else {
			toast.info("No changes detected");
			resetFields();
			resetModified();
		}
	};

	const handleCancel = () => {
		if (
			window.confirm(
				"Are you sure you want to cancel? Any unsaved changes will be lost."
			)
        ) {
            toast.info("Changes cancelled");
			resetFields();
			resetModified();
		}
	};

	const handleDeleteAccount = async () => {
		if (
			window.confirm(
				"Are you sure you want to delete your account? This action cannot be undone."
			)
		) {
			try {
				const response = await sendRequest(
					`/api/user/delete/`,
					"DELETE"
				);

				if (response.statusText === "No Content") {
					Cookies.remove("jwt");
					setIsAuthenticated(false);
					navigate("/", {
						state: { message: "Account deleted successfully" },
					});
				}
			} catch (error) {
				console.error("Error deleting account:", error);
				toast.error("Failed to delete account");
			}
		}
	};

	return (
		<div className={`${styles.accountPage} account-page ${theme}`}>
			<h1>My Account</h1>
			<div className={styles.pictureSection}>
				<h2>Profile Picture</h2>
				<img
					src={profilePicture}
					alt='Profile'
					className={styles.profilePicture}
				/>
			</div>
			<div className={styles.infoSection}>
				<form onSubmit={handleSaveChanges}>
					<h2>Basic Information</h2>
					<label>Username:</label>
					<input
						type={usernameField.type}
						className={styles.text}
						value={usernameField.value}
						onChange={usernameField.onChange}
						onBlur={() => {
							if (
								usernameField.value !== "" &&
								usernameField.value !== initialUsername
							) {
								setModifiedUsername(true);
							} else {
								setModifiedUsername(false);
							}
						}}
					/>
					<label>Email:</label>
					<input
						type={emailField.type}
						className={styles.email}
						value={emailField.value}
						onChange={emailField.onChange}
						onBlur={() => {
							if (
								emailField.value !== "" &&
								emailField.value !== initialEmail
							) {
								setModifiedEmail(true);
							} else {
								setModifiedEmail(false);
							}
						}}
					/>
					<div className={styles.passwordSection}>
						<h2>Password Management</h2>
						<label>New Password:</label>
						<input
							type={newPasswordField.type}
							className={styles.password}
							value={newPasswordField.value}
							onChange={newPasswordField.onChange}
							onBlur={() => {
								if (newPasswordField.value !== "") {
									setModifiedNewPassword(true);
								} else {
									setModifiedNewPassword(false);
								}
							}}
						/>
						<label>Confirm Password:</label>
						<input
							type={confirmPasswordField.type}
							className={styles.password}
							value={confirmPasswordField.value}
							onChange={confirmPasswordField.onChange}
							onBlur={() => {
								if (confirmPasswordField.value !== "") {
									setModifiedConfirmPassword(true);
								} else {
									setModifiedConfirmPassword(false);
								}
							}}
						/>
					</div>
				</form>
			</div>
			<div className={styles.saveCancelSection}>
				<button
					className={styles.saveButton}
					onClick={handleSaveChanges}
				>
					Save Changes
				</button>
				<button className={styles.cancelButton} onClick={handleCancel}>
					Cancel
				</button>
			</div>
			<div className={styles.deleteSection}>
				<button
					className={styles.deleteAccount}
					onClick={handleDeleteAccount}
				>
					Delete Account
				</button>
			</div>
		</div>
	);
};

export default AccountPage;
