import React, { useEffect, useRef, useState } from "react";
import styles from "./Header.module.css";
import { useNavigate, Link } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
	faBookmark,
	faUser,
	faSun,
	faMoon,
} from "@fortawesome/free-solid-svg-icons";
import Cookies from "js-cookie";
import { toast } from "react-toastify";
import logo from "../../images/logo.png";

function Header({ theme, toggleTheme, isAuthenticated, setIsAuthenticated }) {
	const [searchQuery, setSearchQuery] = useState("");
	const [showDropdown, setShowDropdown] = useState(false);
	const dropdownRef = useRef(null);
	const navigate = useNavigate();

	useEffect(() => {
		const handleClickOutside = (event) => {
			if (
				dropdownRef.current &&
				!dropdownRef.current.contains(event.target)
			) {
				setShowDropdown(false);
			}
		};
		document.addEventListener("mousedown", handleClickOutside);
		return () => {
			document.removeEventListener("mousedown", handleClickOutside);
		};
	}, [dropdownRef]);

	const handleInputChange = (e) => {
		setSearchQuery(e.target.value);
	};

	const handleKeyDown = async (e) => {
		if (e.key === "Enter" && searchQuery) {
			e.preventDefault();
			navigate(`/search?q=${searchQuery}`);
		}
	};

	const toggleDropdown = () => {
		setShowDropdown(!showDropdown);
	};

	const handleLogout = () => {
		Cookies.remove("jwt");
		setIsAuthenticated(false);
		navigate("/");
		toast.success("Logout successful!");
	};

	return (
		<header className={`${styles.header} ${theme}`}>
			<Link to='/' className={styles.logoContainer}>
				<img src={logo} alt='Logo' className={styles.logo} />
				<span className={styles.logoText}>SickBeat</span>
			</Link>
			<div className={styles.searchbar}>
				<input
					type='text'
					placeholder='Search...'
					value={searchQuery}
					onChange={handleInputChange}
					onKeyDown={handleKeyDown}
					className={styles.searchInput}
				/>
			</div>
			<nav className={styles.navbar}>
				<ul className={styles.iconList}>
					{isAuthenticated ? (
						<div
							className={styles.dropdownContainer}
							ref={dropdownRef}
						>
							<li>
								<Link
									to='#'
									className={styles.iconLink}
									onClick={toggleDropdown}
								>
									<FontAwesomeIcon icon={faUser} size='lg' />
								</Link>
							</li>
							{showDropdown && (
								<div className={styles.dropdownMenu}>
									<Link
										to='/account'
										className={styles.dropdownItem}
										onClick={toggleDropdown}
									>
										Account
									</Link>

									<Link
										to='/'
										className={styles.dropdownItem}
										onClick={() => {
											handleLogout();
											toggleDropdown();
										}}
									>
										Log Out
									</Link>
								</div>
							)}
						</div>
					) : (
						<li>
							<Link to='/login' className={styles.iconLink}>
								<FontAwesomeIcon icon={faUser} size='lg' />
							</Link>
						</li>
					)}
					{isAuthenticated && (
						<li>
							<Link to='/library' className={styles.iconLink}>
								<FontAwesomeIcon icon={faBookmark} size='lg' />
							</Link>
						</li>
					)}
					<li>
						<button
							className={styles.themeButton}
							onClick={toggleTheme}
						>
							{theme === "light" ? (
								<FontAwesomeIcon icon={faMoon} size='lg' />
							) : (
								<FontAwesomeIcon icon={faSun} size='lg' />
							)}
						</button>
					</li>
				</ul>
			</nav>
		</header>
	);
}

export default Header;
