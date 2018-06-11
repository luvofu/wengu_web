package com.wg.notebook.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.common.Constant;
import com.wg.common.Enum.common.Permission;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.ResponseContent;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.common.utils.dbutils.DeleteUtils;
import com.wg.notebook.domain.Note;
import com.wg.notebook.domain.Notebook;
import com.wg.notebook.domain.Storyline;
import com.wg.notebook.model.request.NotebookRequest;
import com.wg.notebook.model.request.StorylineRequest;
import com.wg.notebook.model.response.NotebookDetailResponse;
import com.wg.notebook.model.response.NotebookNoteResponse;
import com.wg.notebook.model.response.NotebookPersonalResponse;
import com.wg.notebook.model.response.NotebookStorylineResponse;
import com.wg.notebook.utils.NotebookUtils;
import com.wg.user.domain.UserToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2016/9/5.
 */
@Controller
public class NotebookController {

    @Transactional
    @RequestMapping(value = "/api/notebook/personal", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String personal(HttpServletRequest request,
                           HttpServletResponse response, NotebookRequest notebookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(notebookRequest.getToken());
        if (userInfoDao.findOne(notebookRequest.getUserId()) != null) {
            int permission = Utils.getPermission(userToken, notebookRequest.getUserId());
            List<Notebook> notebookList = notebookDao.findByUserIdAndPermissionLessThanOrderByCreatedTimeDesc(notebookRequest.getUserId(), permission + 1);
            List<NotebookPersonalResponse> notebookPersonalResponseList = new ArrayList<NotebookPersonalResponse>();
            for (Notebook notebook : notebookList) {
                notebookPersonalResponseList.add(new NotebookPersonalResponse(notebook));
            }
            responseContent.putData("relationType", permission);
            responseContent.putData("noteBookList", notebookPersonalResponseList);
        } else {
            responseContent.update(ResponseCode.USER_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/notebook/add", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String add(HttpServletRequest request,
                      HttpServletResponse response, NotebookRequest notebookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(notebookRequest.getToken());
        if (notebookRequest.getBookId() != -1) {
            if (notebookDao.findByUserIdAndBookId(userToken.getUserId(), notebookRequest.getBookId()) == null) {
                Notebook notebook = AddUtils.addNotebook(userToken.getUserId(), bookDao.findOne(notebookRequest.getBookId()), notebookRequest.getName());
                if (notebook != null) {
                } else {
                    responseContent.update(ResponseCode.CREATE_FAILD);
                }
            } else {
                responseContent.update(ResponseCode.NOTEBOOK_EXIST);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/notebook/delete", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String delete(HttpServletRequest request,
                         HttpServletResponse response, NotebookRequest notebookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(notebookRequest.getToken());
        Notebook notebook = notebookDao.findOne(notebookRequest.getNotebookId());
        if (userToken.getUserId() == notebook.getUserId()) {
            if (notebook != null) {
                DeleteUtils.deleteNotebook(notebook);
            } else {
                responseContent.update(ResponseCode.NOTEBOOK_NOT_EXIST);
            }
        } else {
            responseContent.update(ResponseCode.ILLEGAL_USER);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/notebook/detail", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String detail(HttpServletRequest request,
                         HttpServletResponse response, NotebookRequest notebookRequest) {
        ResponseContent responseContent = new ResponseContent();
        NotebookDetailResponse notebookDetailResponse;
        UserToken userToken = userTokenDao.findByToken(notebookRequest.getToken());
        Notebook notebook = notebookDao.findOne(notebookRequest.getNotebookId());
        if (notebook != null) {
            int permission = Utils.getPermission(userToken, notebook.getUserId());
            notebookDetailResponse = new NotebookDetailResponse(notebook);
            responseContent.putData("relationType", permission);
            responseContent.putData("notebookDetail", notebookDetailResponse);
        } else {
            responseContent.update(ResponseCode.NOTEBOOK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/notebook/edit", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String edit(HttpServletRequest request,
                       HttpServletResponse response, NotebookRequest notebookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(notebookRequest.getToken());
        Notebook notebook = notebookDao.findOne(notebookRequest.getNotebookId());
        if (notebook != null) {
            if (notebook.getUserId() == userToken.getUserId()) {
                if (StringUtils.isNotBlank(notebookRequest.getName())) {
                    notebook.setName(notebookRequest.getName().trim());
                }
                if (notebookRequest.getDescription() != null) {
                    notebook.setDescription(notebookRequest.getDescription());
                }
                notebook = notebookDao.save(notebook);
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.NOTEBOOK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/notebook/editPermission", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String editPermission(HttpServletRequest request,
                                 HttpServletResponse response, NotebookRequest notebookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(notebookRequest.getToken());
        Notebook notebook = notebookDao.findOne(notebookRequest.getNotebookId());
        if (notebook != null) {
            if (notebook.getUserId() == userToken.getUserId()) {
                if (notebookRequest.getPermission() == Permission.Open.getType()
                        || notebookRequest.getPermission() == Permission.Friend.getType()
                        || notebookRequest.getPermission() == Permission.Personal.getType()) {
                    notebook.setPermission(notebookRequest.getPermission());
                }
                notebook = notebookDao.save(notebook);
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.NOTEBOOK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/notebook/note", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String note(HttpServletRequest request,
                       HttpServletResponse response, NotebookRequest notebookRequest) {
        ResponseContent responseContent = new ResponseContent();
        List<NotebookNoteResponse> notebookNoteResponseList = new ArrayList<NotebookNoteResponse>();
        Notebook notebook = notebookDao.findOne(notebookRequest.getNotebookId());
        if (notebook != null) {
            Pageable pageable = new PageRequest(notebookRequest.getPage(), Constant.PAGE_NUM_LARGE);
            Slice<Note> noteSlice = noteDao.findByNotebookIdOrderByCreatedTimeDesc(notebook.getNotebookId(), pageable);
            for (Note note : noteSlice.getContent()) {
                notebookNoteResponseList.add(new NotebookNoteResponse(note));
            }
            responseContent.putData("noteList", notebookNoteResponseList);
            responseContent.putData("total", noteDao.countByNotebookId(notebook.getNotebookId()));
        } else {
            responseContent.update(ResponseCode.NOTEBOOK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/notebook/addNote", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String addNote(HttpServletRequest request,
                          HttpServletResponse response, NotebookRequest notebookRequest, @RequestParam(value = "imageFile", required = false) MultipartFile file) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(notebookRequest.getToken());
        Notebook notebook = notebookDao.findOne(notebookRequest.getNotebookId());
        if (notebook != null) {
            if (notebook.getUserId() == userToken.getUserId()) {
                Note note = AddUtils.addNote(
                        notebook,
                        notebookRequest.getContent(),
                        null,
                        notebookRequest.getChapter(),
                        notebookRequest.getPages(),
                        notebookRequest.getOtherLocation(),
                        0,
                        file);
                if (note != null) {
                } else {
                    responseContent.update(ResponseCode.CREATE_FAILD);
                }
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.NOTEBOOK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/notebook/deleteNote", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String deleteNote(HttpServletRequest request,
                             HttpServletResponse response, NotebookRequest notebookRequest) {

        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(notebookRequest.getToken());
        Note note = noteDao.findOne(notebookRequest.getNoteId());
        if (note != null) {
            Notebook notebook = notebookDao.findOne(note.getNotebookId());
            if (notebook.getUserId() == userToken.getUserId()) {
                DeleteUtils.deleteNote(note);
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.NOTE_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/notebook/editNote", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String editNote(HttpServletRequest request,
                           HttpServletResponse response, NotebookRequest notebookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(notebookRequest.getToken());
        Note note = noteDao.findOne(notebookRequest.getNoteId());
        if (note != null) {
            Notebook notebook = notebookDao.findOne(note.getNotebookId());
            if (notebook.getUserId() == userToken.getUserId()) {
                if (notebookRequest.getContent() != null) {
                    note.setContent(notebookRequest.getContent());
                }
                if (notebookRequest.getOriginText() != null) {
                    note.setOriginText(notebookRequest.getOriginText());
                }
                if (notebookRequest.getChapter() != null) {
                    note.setChapter(notebookRequest.getChapter());
                }
                if (notebookRequest.getPages() >= 0) {
                    note.setPages(notebookRequest.getPages());
                }
                if (notebookRequest.getOtherLocation() != null) {
                    note.setOtherLocation(notebookRequest.getOtherLocation());
                }
                note = noteDao.save(note);
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.NOTE_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * v104 ** v104 ** v104 ** v104 ** v104 ** v104 ** v104 ** v104 ** v104 ** v104 ** v104 ** v104 ** v104 ** v104 ** v104 ** v104
     **/

    @Transactional
    @RequestMapping(value = "/api/notebook/addStoryline", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String addStoryline(HttpServletRequest request,
                               HttpServletResponse response, StorylineRequest storylineRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(storylineRequest.getToken());
        Notebook notebook = notebookDao.findOne(storylineRequest.getNotebookId());
        String node = storylineRequest.getNode();
        String story = storylineRequest.getStory();
        if (StringUtils.isNotBlank(node) && StringUtils.isNotBlank(story)) {
            if (notebook != null) {
                if (userToken.getUserId() == notebook.getUserId()) {
                    Storyline storyline = AddUtils.addStoryline(
                            notebook, node, story,
                            storylineRequest.getCharacters(),
                            storylineRequest.getPlaces(),
                            NotebookUtils.getStorylineSort(notebook, storylineRequest.getStorylineId()));
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.NOTEBOOK_NOT_EXIST);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/notebook/deleteStoryline", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String deleteStoryline(HttpServletRequest request,
                                  HttpServletResponse response, StorylineRequest storylineRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(storylineRequest.getToken());
        long storylineId = storylineRequest.getStorylineId();
        if (storylineId != -1) {
            Storyline storyline = storylineDao.findOne(storylineId);
            if (storyline != null) {
                if (userToken.getUserId() == storyline.getNotebook().getUserId()) {
                    DeleteUtils.deleteStoryline(storyline);
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.STORYLINE_NOTEXIST);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/notebook/editStoryline", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String editStoryline(HttpServletRequest request,
                                HttpServletResponse response, StorylineRequest storylineRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(storylineRequest.getToken());
        long storylineId = storylineRequest.getStorylineId();
        String node = storylineRequest.getNode();
        String story = storylineRequest.getStory();
        String characters = storylineRequest.getCharacters();
        String places = storylineRequest.getPlaces();
        if (storylineId != -1) {
            Storyline storyline = storylineDao.findOne(storylineId);
            if (storyline != null) {
                if (userToken.getUserId() == storyline.getNotebook().getUserId()) {
                    if (StringUtils.isNotBlank(node)) storyline.setNode(node);
                    if (StringUtils.isNotBlank(story)) storyline.setStory(story);
                    if (characters != null) storyline.setCharacters(characters);
                    if (places != null) storyline.setPlaces(places);
                    storyline = storylineDao.save(storyline);
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.STORYLINE_NOTEXIST);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/notebook/storyline", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String storyline(HttpServletRequest request,
                            HttpServletResponse response, StorylineRequest storylineRequest) {
        ResponseContent responseContent = new ResponseContent();
        List<NotebookStorylineResponse> notebookStorylineResponses = new ArrayList<NotebookStorylineResponse>();
        Notebook notebook = notebookDao.findOne(storylineRequest.getNotebookId());
        if (notebook != null) {
            Pageable pageable = new PageRequest(storylineRequest.getPage(), Constant.PAGE_NUM_LARGE);
            Slice<Storyline> storylineSlice = storylineDao.findByNotebookIdOrderBySortDesc(notebook.getNotebookId(), pageable);
            for (Storyline storyline : storylineSlice.getContent()) {
                notebookStorylineResponses.add(new NotebookStorylineResponse(storyline));
            }
            responseContent.putData("storylineList", notebookStorylineResponses);
        } else {
            responseContent.update(ResponseCode.STORYLINE_NOTEXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/notebook/note_v104", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String note_v104(HttpServletRequest request,
                            HttpServletResponse response, NotebookRequest notebookRequest) {
        ResponseContent responseContent = new ResponseContent();
        List<NotebookNoteResponse> notebookNoteResponseList = new ArrayList<NotebookNoteResponse>();
        Notebook notebook = notebookDao.findOne(notebookRequest.getNotebookId());
        if (notebook != null) {
            Pageable pageable = new PageRequest(notebookRequest.getPage(), Constant.PAGE_NUM_LARGE);
            Slice<Note> noteSlice = noteDao.findByNotebookIdOrderBySortDesc(notebook.getNotebookId(), pageable);
            for (Note note : noteSlice.getContent()) {
                notebookNoteResponseList.add(new NotebookNoteResponse(note));
            }
            responseContent.putData("noteList", notebookNoteResponseList);
        } else {
            responseContent.update(ResponseCode.NOTEBOOK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/notebook/addNote_v104", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String addNote_v104(HttpServletRequest request,
                               HttpServletResponse response, NotebookRequest notebookRequest, @RequestParam(value = "imageFile", required = false) MultipartFile file) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(notebookRequest.getToken());
        Notebook notebook = notebookDao.findOne(notebookRequest.getNotebookId());
        String content = notebookRequest.getContent();
        String originText = notebookRequest.getOriginText();
        int pages = notebookRequest.getPages();
        String chapter = notebookRequest.getChapter();
        if (StringUtils.isNotBlank(content) && (pages > 0 || StringUtils.isNotBlank(chapter))) {
            if (notebook != null) {
                if (userToken.getUserId() == notebook.getUserId()) {
                    Note note = AddUtils.addNote(
                            notebook, content, originText, chapter, pages,
                            null, NotebookUtils.getNoteSort(notebook, notebookRequest.getNoteId()), file);
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.NOTEBOOK_NOT_EXIST);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }
}
