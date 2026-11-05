const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read inspections, voice_actions, or payment_agreements", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("inspections").get());
  await assertFails(unauthDb.collection("voice_actions").get());
  await assertFails(unauthDb.collection("payment_agreements").get());
});

test("Authenticated user: cannot read another user's inspection record", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("inspections").doc("bob_insp_1").set({
      userId: BOB_UID,
      targetType: "Denim Seam",
      hasDefect: true,
      defectLabel: "Skipped Stitch",
      banglaDefectLabel: "বাদ পড়া সেলাই",
      confidenceScore: 0.96,
      severity: "HIGH",
      correctiveAction: "Reroute to Line 3",
      banglaCorrectiveAction: "লাইন ৩-এ স্থানান্তর",
      affectedStation: "Station 2B",
      createdAt: new Date(),
      updatedAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("inspections").doc("bob_insp_1").get());
});

test("Authenticated user: can query their own inspections with userId filter", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("inspections").doc("alice_insp_1").set({
      userId: ALICE_UID,
      targetType: "Denim Seam",
      hasDefect: false,
      defectLabel: "Verified",
      banglaDefectLabel: "ভেরিফাইড",
      confidenceScore: 0.99,
      severity: "NORMAL",
      correctiveAction: "Pass",
      banglaCorrectiveAction: "পাস",
      affectedStation: "Station 1A",
      createdAt: new Date(),
      updatedAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("inspections").where("userId", "==", ALICE_UID).get()
  );
});

test("Authenticated user: fails query without userId filter", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("inspections").get());
});
