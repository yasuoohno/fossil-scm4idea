package org.github.irengrig.test;

import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.VcsConfiguration;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.VcsShowConfirmationOption;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vfs.VirtualFile;
import junit.framework.Assert;
import org.github.irengrig.fossil4idea.commandLine.FCommandName;
import org.github.irengrig.fossil4idea.commandLine.FossilSimpleCommand;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Irina.Chernushina
 * Date: 2/24/13
 * Time: 3:44 PM
 */
public class FossilCheckinTest extends BaseFossilTest {
  @Test
  public void testSimpleCheckin() throws Exception {
    final FossilSimpleCommand settingsCommand = new FossilSimpleCommand(myProject, new File(myBaseVf.getPath()), FCommandName.settings);

    settingsCommand.addParameters("crnl-glob", "");
    settingsCommand.run();

    setStandardConfirmation(VcsConfiguration.StandardConfirmation.ADD, VcsShowConfirmationOption.Value.DO_ACTION_SILENTLY);
    final VirtualFile file = createFileInCommand("a with space.txt", "111\n\r");
    sleep(100);
    myDirtyScopeManager.markEverythingDirty();
    // Y.ONO 2020-05-03 ensureUpToDate can be used for TestOnly.
    // myChangeListManager.ensureUpToDate(false);
    final Change change = myChangeListManager.getChange(file);
    Assert.assertNotNull(change);
    Assert.assertTrue(FileStatus.ADDED.equals(change.getFileStatus()));

    final List<VcsException> commit = myVcs.getCheckinEnvironment().commit(Collections.singletonList(change), "***");
    Assert.assertTrue(commit == null || commit.isEmpty());
    assertNoLocalChanges();
  }

  @Test
  public void testModificationCheckin() throws Exception {
    setStandardConfirmation(VcsConfiguration.StandardConfirmation.ADD, VcsShowConfirmationOption.Value.DO_ACTION_SILENTLY);
    final VirtualFile file = createFileInCommand("a with space.txt", "111");
    sleep(100);
    myDirtyScopeManager.markEverythingDirty();
    // Y.ONO 2020-05-03 ensureUpToDate can be used for TestOnly.
    // myChangeListManager.ensureUpToDate(false);
    final Change change = myChangeListManager.getChange(file);
    Assert.assertNotNull(change);
    Assert.assertTrue(FileStatus.ADDED.equals(change.getFileStatus()));

    final List<VcsException> commit = myVcs.getCheckinEnvironment().commit(Collections.singletonList(change), "***");
    Assert.assertTrue(commit == null || commit.isEmpty());
    assertNoLocalChanges();

    editFileInCommand(myProject, file, "981230213dh2bdbwcwed26y876er32178dewhdjw");

    myDirtyScopeManager.markEverythingDirty();
    // Y.ONO 2020-05-03 ensureUpToDate can be used for TestOnly.
    // myChangeListManager.ensureUpToDate(false);
    final Change changeEdit = myChangeListManager.getChange(file);
    Assert.assertNotNull(changeEdit);
    Assert.assertTrue(FileStatus.MODIFIED.equals(changeEdit.getFileStatus()));

    final List<VcsException> commitEdit = myVcs.getCheckinEnvironment().commit(Collections.singletonList(changeEdit), "***");
    Assert.assertTrue(commitEdit == null || commitEdit.isEmpty());
    assertNoLocalChanges();
  }

  @Test
  public void testRenamedFileCheckin() throws Exception {
    setStandardConfirmation(VcsConfiguration.StandardConfirmation.ADD, VcsShowConfirmationOption.Value.DO_ACTION_SILENTLY);
    final VirtualFile file = createFileInCommand("a with space.txt", "111");
    sleep(100);
    myDirtyScopeManager.markEverythingDirty();
    // Y.ONO 2020-05-03 ensureUpToDate can be used for TestOnly.
    // myChangeListManager.ensureUpToDate(false);
    final Change change = myChangeListManager.getChange(file);
    Assert.assertNotNull(change);
    Assert.assertTrue(FileStatus.ADDED.equals(change.getFileStatus()));

    final List<VcsException> commit = myVcs.getCheckinEnvironment().commit(Collections.singletonList(change), "***");
    Assert.assertTrue(commit == null || commit.isEmpty());
    assertNoLocalChanges();

    final File parent = new File(file.getParent().getPath());
    final String newName = "newName.txt";
    renameFileInCommand(myProject, file, newName);
    editFileInCommand(myProject, file, "23244444444444444");
    Assert.assertTrue(file != null && file.isValid() && newName.equals(file.getName()));

    myDirtyScopeManager.markEverythingDirty();
    // Y.ONO 2020-05-03 ensureUpToDate can be used for TestOnly.
    // myChangeListManager.ensureUpToDate(false);
    final Change changeRenamed = myChangeListManager.getChange(file);
    Assert.assertNotNull(changeRenamed);
    Assert.assertTrue(FileStatus.MODIFIED.equals(changeRenamed.getFileStatus()));
//    Assert.assertTrue(changeRenamed.isMoved());

    final List<VcsException> commitRenamed = myVcs.getCheckinEnvironment().commit(Collections.singletonList(changeRenamed), "***");
    Assert.assertTrue(commitRenamed == null || commitRenamed.isEmpty());
    assertNoLocalChanges();
  }

  @Test
  public void testMovedFileCheckin() throws Exception {
    setStandardConfirmation(VcsConfiguration.StandardConfirmation.ADD, VcsShowConfirmationOption.Value.DO_ACTION_SILENTLY);
    final VirtualFile file = createFileInCommand("a with space.txt", "111");
    sleep(100);
    myDirtyScopeManager.markEverythingDirty();
    // Y.ONO 2020-05-03 ensureUpToDate can be used for TestOnly.
    // myChangeListManager.ensureUpToDate(false);
    final Change change = myChangeListManager.getChange(file);
    Assert.assertNotNull(change);
    Assert.assertTrue(FileStatus.ADDED.equals(change.getFileStatus()));

    final List<VcsException> commit = myVcs.getCheckinEnvironment().commit(Collections.singletonList(change), "***");
    Assert.assertTrue(commit == null || commit.isEmpty());
    assertNoLocalChanges();

    final VirtualFile dir = createDirInCommand(myBaseVf, "dir");
    final File parent = new File(file.getParent().getPath());
    moveFileInCommand(myProject, file, dir);
    editFileInCommand(myProject, file, "23244444444444444");
    Assert.assertTrue(file != null && file.isValid());

    myDirtyScopeManager.markEverythingDirty();
    // Y.ONO 2020-05-03 ensureUpToDate can be used for TestOnly.
    // myChangeListManager.ensureUpToDate(false);
    final Change changeRenamed = myChangeListManager.getChange(file);
    Assert.assertNotNull(changeRenamed);
    Assert.assertTrue(FileStatus.MODIFIED.equals(changeRenamed.getFileStatus()));
//    Assert.assertTrue(changeRenamed.isMoved());

    final List<VcsException> commitRenamed = myVcs.getCheckinEnvironment().commit(Collections.singletonList(changeRenamed), "***");
    Assert.assertTrue(commitRenamed == null || commitRenamed.isEmpty());
    assertNoLocalChanges();
  }

  @Test
  public void testRenameDirCheckin() throws Exception {
    setStandardConfirmation(VcsConfiguration.StandardConfirmation.ADD, VcsShowConfirmationOption.Value.DO_ACTION_SILENTLY);
    final VirtualFile dir = createDirInCommand(myBaseVf, "dir");
    final VirtualFile file = createFileInCommand(dir, "a with space.txt", "111");
    sleep(100);
    myDirtyScopeManager.markEverythingDirty();
    // Y.ONO 2020-05-03 ensureUpToDate can be used for TestOnly.
    // myChangeListManager.ensureUpToDate(false);
    final Change change = myChangeListManager.getChange(file);
    Assert.assertNotNull(change);
    Assert.assertTrue(FileStatus.ADDED.equals(change.getFileStatus()));

    final List<VcsException> commit = myVcs.getCheckinEnvironment().commit(Collections.singletonList(change), "***");
    Assert.assertTrue(commit == null || commit.isEmpty());
    assertNoLocalChanges();

    renameFileInCommand(myProject, dir, "newName");
    editFileInCommand(myProject, file, "23244444444444444");
    Assert.assertTrue(file != null && file.isValid());

    myDirtyScopeManager.markEverythingDirty();
    // Y.ONO 2020-05-03 ensureUpToDate can be used for TestOnly.
    // myChangeListManager.ensureUpToDate(false);
    final Change changeRenamed = myChangeListManager.getChange(file);
    Assert.assertNotNull(changeRenamed);
    Assert.assertTrue(FileStatus.MODIFIED.equals(changeRenamed.getFileStatus()));
//    Assert.assertTrue(changeRenamed.isMoved());

    final List<VcsException> commitRenamed = myVcs.getCheckinEnvironment().commit(Collections.singletonList(changeRenamed), "***");
    Assert.assertTrue(commitRenamed == null || commitRenamed.isEmpty());
    assertNoLocalChanges();
  }
}
