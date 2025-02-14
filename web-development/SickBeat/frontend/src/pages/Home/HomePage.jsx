import React, { useRef, useEffect } from "react";
import Hero from "../../components/Hero/Hero.jsx";
import GenresList from "../../components/GenresList/GenresList.jsx";
import CardList from "../../components/CardList/CardList.jsx";
import HorizontalLine from "../../components/HorizontalLine/HorizontalLine.jsx";
import { useSpotifyGet } from "../../hooks/useSpotifyGet.jsx";
import { useNavigate, useLocation } from "react-router-dom";
import { toast } from "react-toastify";

function HomePage() {
	const navigate = useNavigate();
	const location = useLocation();
	const state = location.state || {};

	useEffect(() => {
		if (state.message) {
			toast.success(state.message);

			navigate(location.pathname, { replace: true, state: {} });
		}
	}, [state, navigate, location.pathname]);

	const { data: newReleases, error: newReleasesError } = useSpotifyGet(
		"/api/spotify/new-releases"
	);
	const { data: topHits, error: topHitsError } = useSpotifyGet(
		"/api/spotify/top-hits"
	);
	const { data: genres, error: genresError } = useSpotifyGet(
		"/api/spotify/genres"
	);
	const genresListRef = useRef(null);

	return (
		<>
			<Hero genresListRef={genresListRef} />
			<CardList
				title='New Releases'
				items={newReleases}
				error={newReleasesError}
			/>
			<HorizontalLine />
			<CardList title='Top Hits' items={topHits} error={topHitsError} />
			<HorizontalLine />
			<GenresList
				ref={genresListRef}
				genres={genres}
				error={genresError}
			/>
		</>
	);
}

export default HomePage;
