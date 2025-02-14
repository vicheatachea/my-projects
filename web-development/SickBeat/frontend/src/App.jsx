import { useState, useEffect } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import "./App.css";
import React from "react";
import Header from "./components/Header/Header.jsx";
import Footer from "./components/Footer/Footer.jsx";
import LoginPage from "./pages/Login/LoginPage.jsx";
import RegisterPage from "./pages/Register/RegisterPage.jsx";
import HomePage from "./pages/Home/HomePage.jsx";
import SearchResultsPage from "./pages/SearchResults/SearchResultsPage.jsx";
import MusicPlayerPage from "./pages/MusicPlayer/MusicPlayerPage.jsx";
import AccountPage from "./pages/Account/AccountPage.jsx";
import LibraryPage from "./pages/Library/LibraryPage.jsx";
import ArtistPage from "./pages/Artist/ArtistPage.jsx";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Cookies from "js-cookie";
import NotFoundPage from "./pages/NotFound/NotFoundPage.jsx";
import CollectionPage from "./pages/Collection/CollectionPage.jsx";

function App() {
	const [theme, setTheme] = useState(() => {
		return localStorage.getItem("theme") || "light";
	});

	const [isAuthenticated, setIsAuthenticated] = useState(() => {
		const token = Cookies.get("jwt");
		if (token) {
			try {
				const user = JSON.parse(atob(token.split(".")[1]));
				return user ? true : false;
			} catch (error) {
				console.error("Error parsing JWT:", error);
				return false;
			}
		}
		return false;
	});

	useEffect(() => {
		document.body.className = theme;
		localStorage.setItem("theme", theme);
	}, [theme]);

	const toggleTheme = () => {
		const newTheme = theme === "light" ? "dark" : "light";
		setTheme(newTheme);
		document.body.className = newTheme; // Apply the theme to the body

		const event = new Event("classChange");
		document.body.dispatchEvent(event);
	};

	return (
		<>
			<BrowserRouter>
				<ToastContainer
					position='bottom-right'
					hideProgressBar={false}
					autoClose={3000}
					newestOnTop
					closeOnClick
					draggable
					theme='colored'
					transition:Slide
				/>
				<Header
					theme={theme}
					toggleTheme={toggleTheme}
					isAuthenticated={isAuthenticated}
					setIsAuthenticated={setIsAuthenticated}
				/>
				<Routes>
					<Route path='/' element={<HomePage />} />
					<Route
						path='/login'
						element={
							isAuthenticated ? (
								<Navigate to='/' />
							) : (
								<LoginPage
									setIsAuthenticated={setIsAuthenticated}
								/>
							)
						}
					/>
					<Route
						path='/register'
						element={
							isAuthenticated ? (
								<Navigate to='/' />
							) : (
								<RegisterPage
									setIsAuthenticated={setIsAuthenticated}
								/>
							)
						}
					/>
					<Route path='/search' element={<SearchResultsPage />} />
					<Route path='/play' element={<MusicPlayerPage />} />
					<Route path='/collection/:type/:id' element={<CollectionPage isAuthenticated={isAuthenticated} />} />
					<Route path='/artist/:id' element={<ArtistPage />} />
					<Route
						path='/account'
						element={
							isAuthenticated ? (
								<AccountPage
									setIsAuthenticated={setIsAuthenticated}
								/>
							) : (
								<Navigate to='/login' />
							)
						}
					/>
					<Route
						path='/library'
						element={
							isAuthenticated ? (
								<LibraryPage />
							) : (
								<Navigate to='/login' />
							)
						}
					/>
					<Route path='/*' element={<NotFoundPage />} />
				</Routes>
				<Footer theme={theme} />
			</BrowserRouter>
		</>
	);
}

export default App;