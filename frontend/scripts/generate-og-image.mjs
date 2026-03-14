/**
 * Génère og-image.png (1200×630) à partir de og-image.svg pour une meilleure compatibilité Open Graph.
 * À lancer une fois : npm run generate:og
 */
import { readFileSync, writeFileSync } from "fs";
import { fileURLToPath } from "url";
import { dirname, join } from "path";
import sharp from "sharp";

const __dirname = dirname(fileURLToPath(import.meta.url));
const root = join(__dirname, "..");
const svgPath = join(root, "public", "images", "og-image.svg");
const pngPath = join(root, "public", "images", "og-image.png");

const svg = readFileSync(svgPath);

sharp(Buffer.from(svg))
  .resize(1200, 630)
  .png()
  .toFile(pngPath)
  .then(() => console.log("Generated public/images/og-image.png (1200×630)"))
  .catch((err) => {
    console.error("Error generating OG image:", err.message);
    process.exit(1);
  });
