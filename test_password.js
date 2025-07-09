const bcrypt = require("bcrypt");

async function testPasswords() {
  console.log("🔍 Test des mots de passe BCrypt...\n");

  const testPasswords = [
    { username: "admin", password: "admin123" },
    { username: "user", password: "user123" },
  ];

  for (const test of testPasswords) {
    // Générer un nouveau hash
    const hash = await bcrypt.hash(test.password, 10);
    console.log(`${test.username}:`);
    console.log(`  Mot de passe: ${test.password}`);
    console.log(`  Hash BCrypt: ${hash}`);
    console.log("");
  }

  // Tester les hashes existants
  const existingHashes = {
    admin: "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa",
    user: "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a",
  };

  console.log("🔍 Test des hashes existants...\n");

  for (const [username, hash] of Object.entries(existingHashes)) {
    const password = username === "admin" ? "admin123" : "user123";
    const isValid = await bcrypt.compare(password, hash);
    console.log(`${username}: ${isValid ? "✅ VALIDE" : "❌ INVALIDE"}`);
  }
}

testPasswords().catch(console.error);
