package de.uniwue.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import de.uniwue.controller.PreprocessingController;
import de.uniwue.helper.ProcessFlowHelper;
import de.uniwue.model.ProcessFlowData;

/**
 * Controller class for pages of process flow module
 * Use response.setStatus to trigger AJAX fail (and therefore show errors)
 */
@Controller
public class ProcessFlowController {
    /**
     * Response to the request to send the content of the /ProcessFlow page
     *
     * @param session Session of the user
     * @return Returns the content of the /ProcessFlow page
     */
    @RequestMapping("/ProcessFlow")
    public ModelAndView show(HttpSession session) {
        ModelAndView mv = new ModelAndView("processFlow");

        String projectDir = (String)session.getAttribute("projectDir");
        if (projectDir == null) {
            mv.addObject("error", "Session expired.\nPlease return to the Project Overview page.");
            return mv;
        }

        return mv;
    }

    /**
     * Helper functions to verify that settings for every executed process are provided
     *
     * @param processesToExecute Names of the processes that should be executed
     * @param processSettings Settings for each process
     * @param response Response to the request
     */
    public void validateSettings(
                List<String> processesToExecute, Map<String, Map<String, Object>> processSettings,
                HttpServletResponse response
            ) {
        for(String process : processesToExecute) {
            if (!processSettings.containsKey(process)) {
                response.setStatus(533); //533 = Custom: Not all necessary process settings were passed
                return;
            }
        }
    }

    /**
     * Helper function to execute the Preprocessing process via its Controller
     *
     * @param pageIds Identifiers of the pages (e.g 0002,0003)
     * @param cmdArgs Command line arguments for the process
     * @param session Session of the user
     * @param response Response to the request
     */
    public void doPreprocessing(String[] pageIds, Object cmdArgs, HttpSession session, HttpServletResponse response) {
        if (pageIds.length == 0 || !(cmdArgs instanceof List<?>)) {
            response.setStatus(531); //531 = Custom: Exited due to invalid input
            return;
        }

        // Convert from List to Array
        @SuppressWarnings("unchecked")
        List<String> cmdArgsList = (List<String>)cmdArgs;
        String[] cmdArgsArr = new String[cmdArgsList.size()];
        cmdArgsArr = cmdArgsList.toArray(cmdArgsArr);

        new PreprocessingController().execute(pageIds, cmdArgsArr, session, response);
    }

    /**
     * Helper function to execute the Despeckling process via its Controller
     *
     * @param pageIds Identifiers of the pages (e.g 0002,0003)
     * @param maxContourRemovalSize Maximum size of the contours to be removed
     * @param session Session of the user
     * @param response Response to the request
     */
    public void doDespeckling(String[] pageIds, Object maxContourRemovalSize, HttpSession session, HttpServletResponse response) {
        if (pageIds.length == 0) {
            response.setStatus(531); //531 = Custom: Exited due to invalid input
            return;
        }

        Double maxContourRemovalSizeDouble = Double.parseDouble((String)maxContourRemovalSize);
        new DespecklingController().execute(pageIds, maxContourRemovalSizeDouble, session, response);
    }

    /**
     * Helper function to execute the Segmentation process via its Controller
     *
     * @param pageIds Identifiers of the pages (e.g 0002,0003)
     * @param segmentationImageType Type of the images (binary,despeckled)
     * @param replace If true, replaces the existing image files
     * @param session Session of the user
     * @param response Response to the request
     */
    public void doSegmentation(
                String[] pageIds, Object segmentationImageType, Object replace,
                HttpSession session, HttpServletResponse response
            ) {
        if (pageIds.length == 0) {
            response.setStatus(531); //531 = Custom: Exited due to invalid input
            return;
        }

        Boolean replaceBoolean = Boolean.parseBoolean((String)replace);
        new SegmentationController().execute(pageIds, (String)segmentationImageType, replaceBoolean, session, response);
    }

    /**
     * Helper function to execute the RegionExtraction process via its Controller
     *
     * @param pageIds Identifiers of the pages (e.g 0002,0003)
     * @param spacing
     * @param useSpacing
     * @param avgBackground
     * @param session Session of the user
     * @param response Response to the request
     */
    public void doRegionExtraction(
                String[] pageIds, Object spacing, Object useSpacing, Object avgBackground,
                HttpSession session, HttpServletResponse response
            ) {
        if (pageIds.length == 0) {
            response.setStatus(531); //531 = Custom: Exited due to invalid input
            return;
        }

        Integer spacingInteger = Integer.parseInt((String)spacing);
        Boolean useSpacingBoolean = Boolean.parseBoolean((String)useSpacing);
        Boolean avgBackgroundBoolean = Boolean.parseBoolean((String)avgBackground);
        new RegionExtractionController().execute(pageIds, spacingInteger, useSpacingBoolean, avgBackgroundBoolean, session, response);
    }

    /**
     * Helper function to execute the LineSegmentation process via its Controller
     *
     * @param pageIds Identifiers of the pages (e.g 0002,0003)
     * @param cmdArgs Command line arguments for the process
     * @param session Session of the user
     * @param response Response to the request
     */
    public void doLineSegmentation(String[] pageIds, Object cmdArgs, HttpSession session, HttpServletResponse response) {
        if (pageIds.length == 0 || !(cmdArgs instanceof List<?>)) {
            response.setStatus(531); //531 = Custom: Exited due to invalid input
            return;
        }

        // Convert from List to Array
        @SuppressWarnings("unchecked")
        List<String> cmdArgsList = (List<String>)cmdArgs;
        String[] cmdArgsArr = new String[cmdArgsList.size()];
        cmdArgsArr = cmdArgsList.toArray(cmdArgsArr);

        new LineSegmentationController().execute(pageIds, cmdArgsArr, session, response);
    }

    /**
     * Helper function to execute the Recognition process via its Controller
     *
     * @param pageIds Identifiers of the pages (e.g 0002,0003)
     * @param cmdArgs Command line arguments for the process
     * @param session Session of the user
     * @param response Response to the request
     */
    public void doRecognition(String[] pageIds, Object cmdArgs, HttpSession session, HttpServletResponse response) {
        if (pageIds.length == 0 || !(cmdArgs instanceof List<?>)) {
            response.setStatus(531); //531 = Custom: Exited due to invalid input
            return;
        }

        // Convert from List to Array
        @SuppressWarnings("unchecked")
        List<String> cmdArgsList = (List<String>)cmdArgs;
        String[] cmdArgsArr = new String[cmdArgsList.size()];
        cmdArgsArr = cmdArgsList.toArray(cmdArgsArr);

        new RecognitionController().execute(pageIds, cmdArgsArr, session, response);
    }

    /**
     * Determines if the process flow execution needs to be exited
     *
     * @param session Session of the user
     * @param response Response to the request
     * @return Exit decision
     */
    public boolean needsExit(HttpSession session, HttpServletResponse response) {
        // Error in process execution
        if (response.getStatus() != 200) {
            session.setAttribute("currentProcess", "");
            return true;
        }

        // Cancel of process flow execution was triggered
        Boolean cancelProcessFlow = (Boolean) session.getAttribute("cancelProcessFlow");
        if (cancelProcessFlow == true) {
            session.setAttribute("currentProcess", "");
            return true;
        }

        return false;
    }

    /**
     * Response to the request to execute the processflow
     *
     * @param processFlowData Necessary data for processflow execution
     * @param session Session of the user
     * @param response Response to the request
     */
    @RequestMapping(value = "/ajax/processFlow/execute", method = RequestMethod.POST)
    public @ResponseBody void execute(
               @RequestBody ProcessFlowData processFlowData,
               HttpSession session, HttpServletResponse response
           ) {
        // Check that necessary session variables are set
        String projectDir = (String) session.getAttribute("projectDir");
        String imageType  = (String) session.getAttribute("imageType");
        if (projectDir == null || projectDir.isEmpty() || imageType == null || imageType.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        // Check that all variables were passed in request
        String[] pageIds = processFlowData.getPageIds();
        List<String> processes = processFlowData.getProcessesToExecute();
        Map<String, Map<String, Object>> processSettings = processFlowData.getProcessSettings();
        if (pageIds == null || processes == null || processSettings == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // There already is a process flow execution in progress
        String currentProcess = (String) session.getAttribute("currentProcess");
        if (currentProcess != null && !currentProcess.isEmpty()) {
            response.setStatus(532); //532 = Custom: Process Flow execution still running
            return;
        }

        session.setAttribute("cancelProcessFlow", false);

        // Verify that settings for every executed process are provided
        validateSettings(processes, processSettings, response);
        if (needsExit(session, response))
            return;

        /*
         * Execute all processes consecutively
         * Store current executing process in session as point of reference
         * Determine the results after each process and use them in the next one
         * Check after execution if the process flow needs to stop and return
         */
        ProcessFlowHelper processFlowHelper = new ProcessFlowHelper(projectDir, imageType);

        if (processes.contains("preprocessing")) {
            session.setAttribute("currentProcess", "preprocessing");
            doPreprocessing(pageIds, processSettings.get("preprocessing").get("cmdArgs"), session, response);
            if (needsExit(session, response))
                return;
        }

        if (processes.contains("despeckling")) {
            session.setAttribute("currentProcess", "despeckling");
            pageIds = processFlowHelper.getValidPageIds(pageIds, "preprocessing");
            doDespeckling(pageIds, processSettings.get("despeckling").get("maxContourRemovalSize"), session, response);
            if (needsExit(session, response))
                return;
        }

        if (processes.contains("segmentation")) {
            session.setAttribute("currentProcess", "segmentation");
            pageIds = processFlowHelper.getValidPageIds(pageIds, "preprocessing");
            Map<String, Object> settings = processSettings.get("segmentation");
            doSegmentation(pageIds, settings.get("imageType"), settings.get("replace"), session, response);
            if (needsExit(session, response))
                return;
        }

        if (processes.contains("regionExtraction")) {
            session.setAttribute("currentProcess", "regionExtraction");
            pageIds = processFlowHelper.getValidPageIds(pageIds, "segmentation");
            Map<String, Object> settings = processSettings.get("regionExtraction");
            doRegionExtraction(pageIds, settings.get("spacing"), settings.get("usespacing"), settings.get("avgbackground"), session, response);
            if (needsExit(session, response))
                return;
        }

        if (processes.contains("lineSegmentation")) {
            session.setAttribute("currentProcess", "lineSegmentation");
            pageIds = processFlowHelper.getValidPageIds(pageIds, "regionExtraction");
            doLineSegmentation(pageIds, processSettings.get("lineSegmentation").get("cmdArgs"), session, response);
            if (needsExit(session, response))
                return;
        }

        if (processes.contains("recognition")) {
            session.setAttribute("currentProcess", "recognition");
            pageIds = processFlowHelper.getValidPageIds(pageIds, "lineSegmentation");
            doRecognition(pageIds, processSettings.get("recognition").get("cmdArgs"), session, response);
            if (needsExit(session, response))
                return;
        }

        session.setAttribute("currentProcess", "");
    }

    /**
     * Get the process that is currently executed in the process flow
     *
     * @param session Session of the user
     * @return Currently executed process name
     */
    @RequestMapping(value = "/ajax/processFlow/current", method = RequestMethod.GET)
    public @ResponseBody String currentProcess(HttpSession session) {
        String currentProcess = (String) session.getAttribute("currentProcess");
        if (currentProcess == null) {
            return "";
        }

        return currentProcess;
    }

    /**
     * Indicates that the process flow execution should be cancelled
     * Cancels currently executed process as well to initiate cancellation
     *
     * @param terminate Determines if the current process should be terminated or not
     * @param session Session of the user
     * @param response Response to the request
     */
    @RequestMapping(value = "/ajax/processFlow/cancel", method = RequestMethod.POST)
    public @ResponseBody void cancel(
                @RequestParam(value = "terminate", required = false) Boolean terminate,
                HttpSession session, HttpServletResponse response
            ) {
        // First check if there is a process flow execution running at all
        String currentProcess = (String) session.getAttribute("currentProcess");
        if (currentProcess == null || currentProcess.isEmpty()) {
            response.setStatus(534); //534 = Custom: No Process Flow execution to cancel
            return;
        }

        // Set cancel information
        session.setAttribute("cancelProcessFlow", true);

        if (terminate != null && terminate == true) {
            // Cancel current process
            switch(currentProcess) {
                case "preprocessing":    new PreprocessingController().cancel(session, response); break;
                case "despeckling":      new DespecklingController().cancel(session, response); break;
                case "segmentation":     new SegmentationController().cancel(session, response); break;
                case "regionExtraction": new RegionExtractionController().cancel(session, response); break;
                case "lineSegmentation": new LineSegmentationController().cancel(session, response); break;
                case "recognition":      new RecognitionController().cancel(session, response); break;
                default: return;
            }
        }
    }
}
