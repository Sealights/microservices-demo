const axios = require("axios");
const {jwtDecode} = require("jwt-decode");
const fs = require("fs");

const apiToken = process.env.SL_API_TOKEN;
const labid = process.env.SL_LAB_ID;
const teststage = process.env.SL_TEST_STAGE;

const decoded = jwtDecode(apiToken); // Agent Token
const baseUrl = decoded["x-sl-server"]; // Base url of the backend

const testSessionsV1Instance = axios.create({
  baseURL: baseUrl.replace("/api", "/sl-api/v1/test-sessions"),
  headers: {
    Authorization: `Bearer ${apiToken}`,
  },
});

const testSessionsV2Instance = axios.create({
  baseURL: baseUrl.replace("/api", "/sl-api/v2/test-sessions"),
  headers: {
    Authorization: `Bearer ${apiToken}`,
  },
});

module.exports = {
  createTestSession: async () => {
    const { data } = await testSessionsV1Instance.post("/", {
      testStage: teststage,
      labId: labid,
    });
    return data;
  },
  endTestSession: (testSessionId) => {
    return testSessionsV1Instance.delete(`/${testSessionId}`);
  },
  sendTestEvent: (testSessionId, name, start, end, status) => {
    return testSessionsV2Instance.post(`/${testSessionId}`, [
      {
        name,
        start,
        end,
        status,
      },
    ]);
  },
};