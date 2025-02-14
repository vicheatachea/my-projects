require("dotenv").config();
const jwt = require("jsonwebtoken");

function authenticateToken(req, res, next) {
	try {
		const token =
			req.cookies["jwt"] ||
			(req.headers["authorization"] &&
				req.headers["authorization"].split(" ")[1]);

		if (!token) {
			return res.status(401).json({ message: "Access token is missing" });
		}

		jwt.verify(token, process.env.JWT_SECRET, (err, user) => {
			if (err) {
				return res.status(403).json({ message: "Invalid or expired token" });
			}
			req.user = user;
			next();
		});
	} catch (err) {
		console.log(err);
		return res.sendStatus(500);
	}
}

module.exports = authenticateToken;
